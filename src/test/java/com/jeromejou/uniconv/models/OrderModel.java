package com.jeromejou.uniconv.models;

public class OrderModel
{
	private String code;
	private String status;
	private AddressModel deliveryAddress;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public AddressModel getDeliveryAddress()
	{
		return deliveryAddress;
	}

	public void setDeliveryAddress(final AddressModel deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}
}