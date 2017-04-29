from __future__ import unicode_literals

from django.db import models

# Create your models here.
from users.models import User, BankInfo, BankAccountDetails, UpiDetails
from vendors.models import Bill, Stores


class Transaction(models.Model):

    BANK_TO_BANK = 0
    UPI = 1
    MOBILE = 2
    TRANSACTION_TYPES = ((BANK_TO_BANK, 'bank_to_bank'),
                         (UPI, 'upi'),
                         (MOBILE, 'mobile'))

    SUCCESS = 1
    FAIL = 0
    PROCESSING = 2
    TRANSACTION_STATE = ((FAIL, 'fail'),
                         (SUCCESS, 'success'),
                         (PROCESSING, 'processing'))

    sender = models.ForeignKey(User, on_delete=models.CASCADE)
    receiver = models.ForeignKey(User, related_name='receiver', null=True)
    transaction_type = models.SmallIntegerField(choices=TRANSACTION_TYPES)
    created_on = models.DateTimeField(auto_now=True)
    transaction_state = models.SmallIntegerField(choices=TRANSACTION_STATE)
    transaction_state_details = models.CharField(max_length=500, null=True, blank=True)
    amount = models.IntegerField(null=False)
    bill = models.ForeignKey(Bill, null=True)
    text = models.CharField(max_length=150, blank=True, null=True)
    store = models.ForeignKey(Stores, null=True, blank=True)


class AccountDetails(models.Model):
    sender_bank = models.ForeignKey(BankAccountDetails, on_delete=models.SET_NULL, null=True)
    transaction = models.ForeignKey(Transaction, on_delete=models.CASCADE)
    holder_name = models.CharField(max_length=50, null=False)
    account_no = models.CharField(max_length=30, null=False)
    ifsc_code = models.CharField(max_length=30, null=False)
    bank = models.ForeignKey(BankInfo, null=True, default=1)


class Upi(models.Model):
    sender_upi = models.ForeignKey(UpiDetails, on_delete=models.CASCADE, null=True)
    receiver_upi_adderss = models.CharField(max_length=100, null=False)
    transaction = models.ForeignKey(Transaction, null=True)


class Mobile(models.Model):
    sender_account = models.ForeignKey(BankAccountDetails, null=True)
    receiver_account = models.ForeignKey(BankAccountDetails, related_name='receiver_account', null=True)
    transaction = models.ForeignKey(Transaction, null=True)




