import json
from datetime import datetime

import requests

from common.cons import WSDL_URL
from fin_helper_backend.settings import POSTGRES_DATE_TIME_FORMAT
from invoice.cons import FISC_TRIES
from invoice.exceptions import NoDataFromFiscalisation
from seller.models import Seller

WSDL_URL = ''

def get_fisc_response(validated_data):
    """
    :info
        function that makes request to fisc server to take data for invoice. It makes a number of max tries:FISC_TRIES before
        giving up and raising NoDataFromFiscalisation since fisc server takes some tries before sending the correct response
    :param validated_data: dict(iic,dateRegistered,nuis) with data needed to make request to fisc server
    :return: str -> json format with response data or raise NoDataFromFiscalisation no data was found
    """
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    for i in range(FISC_TRIES):
        fisc_response = requests.post(WSDL_URL, data=validated_data, headers=headers, verify=False, timeout=60)
        if fisc_response.status_code != 404 and fisc_response.text:
            return fisc_response.text
    raise NoDataFromFiscalisation(
        'There was no data from fiscalisation server. Please try again. If problem persists make sure the invoice is fiscalised!')


def get_fisc_invoice_data(json_text, user, fiscal_invoice_url):
    """
    :info
        Function takes the response from fisc server and utilises it to return a formatted dict
        holding the necessary info for building an invoice with its units and payment
    :param user: user_obj: authentication.models.User
    :param json_text: dict()
    :return: dict()
    """
    fisc_data = json.loads(json_text)
    currency = fisc_data['currency']
    data = {
        'customer': user.id,
        'seller': get_seller_data(fisc_data['seller']),
        'iic': fisc_data['iic'],
        'fic': fisc_data['fic'],
        'date_registered': get_invoice_date_time_created(fisc_data['dateTimeCreated']),
        'invoice_units': get_invoice_items(fisc_data['items']),
        'invoice_payment': get_invoice_payment(currency, user, fisc_data["totalPriceWithoutVAT"],
                                               fisc_data['paymentMethod']),
        'fiscal_invoice_url': fiscal_invoice_url
    }
    return data


def get_seller_data(seller_json):
    return {
        'nipt': seller_json['idNum'].strip(),
        'name': seller_json['name'].strip(),
        'address': seller_json['address'].strip() if seller_json['address'] else None,
        'town': seller_json['town'].strip() if seller_json.get("town") else "TIRANE",
        'country': seller_json['country'].strip() if seller_json.get("country") else "ALB"
    }


def get_invoice_date_time_created(date_iso_format: str):
    date_iso_format = date_iso_format.replace('.000+0000', '')
    return datetime.strptime(date_iso_format, POSTGRES_DATE_TIME_FORMAT)


def get_invoice_items(items_json):
    return [{'name': item['name'], 'code': item['code'], 'measuring_unit': item['unit'],
             'quantity': item['quantity'], 'unit_price_before_vat': round(item['unitPriceBeforeVat'], 2),
             'unit_price_after_vat': round(item['unitPriceAfterVat'], 2), 'price_before_vat': round(item['priceBeforeVat'], 2),
             'price_after_vat': round(item['priceAfterVat'], 2)} for item in items_json]


def get_invoice_payment(currency, user, total_price_before_vat, payment_items):
    if not currency or currency.get("code") != user.country.currency:
        # we compare it with country code because euro invoices should be saved only with converted amount and ALL
        # apparently saving in euro is not necessary weirdly enough
        currency = user.country.currency
    # sum of every payment object since data form fisc server are shallow
    # as per the true payment type used, so concatenate all payment methods as ONE
    # (i.e. paid by credit card still noted as banknotes and coins in most cases)
    amount = 0
    for i in payment_items:
        amount += i.get("amount")
    return {'type': payment_items[0]['type'], 'type_code': payment_items[0]['typeCode'], 'total_amount': amount,
            'currency': currency, 'total_amount_before_vat': total_price_before_vat}


def get_seller(seller_data):
    """
        Function that get dict() with seller info and checks if seller is in db or if needs to be created.
        Return newly created Seller or the one found in db
    :param seller_data: dict()
    :return: seller: Seller()
    """
    return Seller.objects.get_or_create(nipt=seller_data.pop('nipt'), defaults={**seller_data})
