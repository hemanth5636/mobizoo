from __future__ import unicode_literals

from django.contrib.auth.base_user import AbstractBaseUser
from django.db import models

# Create your models here.
from django.utils.text import slugify


class User(AbstractBaseUser):
    MALE = 1
    FEMALE = 0
    GENDER_TYPES = (
        (FEMALE, 'female'),
        (MALE, 'male')
    )

    ACTIVE = 1
    INACTIVE = 0
    ACCOUNT_STATE_TYPES = ((ACTIVE,'active'),
                           (INACTIVE, 'inactive'))
    CUSTOMER = 0
    VENDOR = 1
    VENDOR_MANAGER = 2
    VENDOR_EMPLOYEE = 3
    ACCOUNT_TYPES = ((CUSTOMER,'customer'),
                     (VENDOR, 'vendor'),
                     (VENDOR_MANAGER, 'vendor_manager'),
                     (VENDOR_EMPLOYEE, 'vendor_employee'))

    first_name = models.CharField(max_length=150, null=False)
    last_name = models.CharField(max_length=150, null=False)
    email = models.EmailField(max_length=200, null=False, unique=True)
    mobile = models.CharField(max_length=10, null=True)
    details = models.CharField(max_length=500, null=True)
    otp_verified = models.BooleanField(default=False)
    otp = models.CharField(max_length=4, null=True)
    gender = models.SmallIntegerField(choices=GENDER_TYPES, null=True, default=0)
    dob = models.DateField(null=True)
    joined = models.DateTimeField(auto_now=True, null=False)
    account_state_type = models.SmallIntegerField(choices=ACCOUNT_STATE_TYPES, default=INACTIVE)
    user_type = models.SmallIntegerField(choices=ACCOUNT_TYPES)
    created_by = models.SmallIntegerField(null=True)
    created_on = models.DateTimeField(null=True, auto_now=True)
    profile_pic_url = models.CharField(max_length=200, null=True, blank=True)
    pin = models.CharField(max_length=4, blank=True, null=True)
    USERNAME_FIELD = 'email'

    def save(self, *args, **kwargs):

        super(User, self).save(*args, **kwargs)

    def get_full_name(self):
        # The user is identified by their email address
        return self.email

    def __unicode__(self):
        return self.email


class UpiDetails(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    virtual_address = models.CharField(max_length=150, null=False)
    created_on = models.DateTimeField(auto_now=True)



class BankInfo(models.Model):
    name = models.CharField(max_length=100)
    details = models.CharField(max_length=150)


class BankAccountDetails(models.Model):
    ACTIVE = 1
    INACTIVE = 0
    ACCOUNT_STATE = ((INACTIVE, 'inactive'),
                     (ACTIVE, 'active'))

    user = models.ForeignKey(User, on_delete=models.CASCADE)
    bank = models.ForeignKey(BankInfo, on_delete=models.CASCADE)
    account_no = models.CharField(max_length=11, null=False)
    ifsc_code = models.CharField(max_length=50, null=False)
    holder_name = models.CharField(max_length=50, null=False)
    account_state = models.SmallIntegerField(choices=ACCOUNT_STATE, default=0)
