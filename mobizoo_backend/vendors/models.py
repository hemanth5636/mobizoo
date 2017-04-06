from __future__ import unicode_literals

from django.db import models

# Create your models here.
from users.models import User


class CompanyCategory(models.Model):
    category_name = models.CharField(max_length=100, null=False)
    category_details = models.CharField(max_length=150, null=True)
    created_on = models.DateTimeField(auto_now=True)


class Vendor(models.Model):
    user = models.ForeignKey(User)
    company_name = models.CharField(max_length=50, null=False)
    company_details = models.CharField(max_length=100, null=False)
    company_category = models.ForeignKey(CompanyCategory, on_delete=models.CASCADE)
    created_on = models.DateTimeField(auto_now=True)


class Stores(models.Model):
    store_name = models.CharField(max_length=100, null=False)
    store_details = models.CharField(max_length=200, null=False)
    address_place = models.CharField(max_length=100, null=True)
    address_pincode = models.CharField(max_length=6, null=False)
    address_city = models.CharField(max_length=100, null=False)
    address_state = models.CharField(max_length=100, null=False)
    location = models.CharField(max_length=200, null=True)
    vendor = models.ForeignKey(Vendor, on_delete=models.CASCADE)
    created_on = models.DateTimeField(auto_now=True)
    created_by = models.ForeignKey(User, on_delete=models.CASCADE)


class ContactInfo(models.Model):
    MOBILE = 0
    EMAIL = 1
    WEBSITE = 2
    CONTACT_TYPES = ((MOBILE, 'mobile'),
                     (EMAIL, 'email'),
                     (WEBSITE, 'web_site_url'))

    VENDOR = 0
    STORE = 1
    TYPES = ((VENDOR, 'vendor'),
             (STORE, 'store'))

    contact_type = models.SmallIntegerField(choices=CONTACT_TYPES, null=True)
    contact_details = models.CharField(max_length=200, null=False)
    created_by = models.ForeignKey(User, on_delete=models.CASCADE)
    created_on = models.DateTimeField(auto_now=True)
    type = models.SmallIntegerField(choices=TYPES, null=False)
    source_id = models.SmallIntegerField(null=False)


class Bill(models.Model):

    PURCHASE = 0
    MONEY_TRANSFER = 1
    BILL_TYPES = ((PURCHASE, 'purchase'),
                  (MONEY_TRANSFER, 'money_transfer'))

    sender = models.ForeignKey(User, null=True, related_name='user')
    total_amount = models.IntegerField(null=False)
    receiver = models.ForeignKey(User, on_delete=models.CASCADE, null=True)
    store = models.ForeignKey(Stores, on_delete=models.CASCADE, null=True)
    time_stamp = models.DateTimeField(auto_now=True)
    bill_type = models.SmallIntegerField(choices=BILL_TYPES, default=0)
    text = models.CharField(max_length=150, null=True, blank=True)
    bill_category = models.CharField(max_length=80, null=True, blank=True)


class BillingItems(models.Model):
    bill = models.ForeignKey(Bill, on_delete=models.CASCADE)
    product_name = models.CharField(max_length=50, null=True)
    product_details = models.CharField(max_length=100, null=True)
    purchase_quantity = models.CharField(max_length=10, null=True)
    price_per = models.SmallIntegerField( null=True)
    product_category = models.CharField(max_length=70, null=True)
    discount = models.CharField(max_length=10, null=True)
    time_stamp = models.DateTimeField(auto_now=True)
    total_price = models.SmallIntegerField(null=True)

