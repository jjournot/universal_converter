package com.jeromejou.uniconv;

import java.util.LinkedHashMap;

import com.jeromejou.uniconv.models.AdditionalInfoModel;
import com.jeromejou.uniconv.models.AddressModel;
import com.jeromejou.uniconv.models.OrderModel;
import com.jeromejou.uniconv.models.PersonalInfoModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UniversalConverterTest
{
	private static final String ORDER_CODE = "myOrder";
	private static final String FIRST_NAME = "Homer";
	private static final String LAST_NAME = "Simpson";
	private static final String EMAIL = "homer.simpson@sap.com";
	private static final String STATUS = "CANCELLED";
	private static final String COMMENT = "No comment";
	private static final String WEIGHT = "75 KG";

	private OrderModel orderModel = new OrderModel();
	private LinkedHashMap mapping = new LinkedHashMap();


	@Before
	public void setup()
	{
		orderModel.setCode(ORDER_CODE);
		orderModel.setStatus(STATUS);

		PersonalInfoModel personalInfo = new PersonalInfoModel();
		personalInfo.setWeight(WEIGHT);

		AdditionalInfoModel additionalInfo = new AdditionalInfoModel();
		additionalInfo.setComment(COMMENT);
		additionalInfo.setPersonalInfo(personalInfo);

		AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setFirstname(FIRST_NAME);
		deliveryAddress.setLastname(LAST_NAME);
		deliveryAddress.setEmail(EMAIL);
		deliveryAddress.setAdditionalInfo(additionalInfo);
		orderModel.setDeliveryAddress(deliveryAddress);

		mapping.put("Code", "Id");
		mapping.put("Status", "Status");
	}

	@Test
	public void testConvertNoHierarchy_success()
	{
		LinkedHashMap result = UniversalConverter.convert(orderModel, mapping);
		Assert.assertEquals(ORDER_CODE, result.get("Id"));
		Assert.assertEquals(STATUS, result.get("Status"));
	}

	@Test
	public void testConvertDate_success()
	{

	}

	@Test
	public void testConvertHierarchy1Level_success()
	{
		mapping.put("DeliveryAddress.Firstname", "DeliveryLocation.FirstName");
		mapping.put("DeliveryAddress.Lastname", "DeliveryLocation.LastName");
		LinkedHashMap result = UniversalConverter.convert(orderModel, mapping);
		Assert.assertEquals(FIRST_NAME, ((LinkedHashMap) result.get("DeliveryLocation")).get("FirstName"));
		Assert.assertEquals(LAST_NAME, ((LinkedHashMap) result.get("DeliveryLocation")).get("LastName"));
	}

	@Test
	public void testConvertHierarchy2Level_success()
	{
		mapping.put("DeliveryAddress.Email", "DeliveryLocation.OtherInformation.Email");
		mapping.put("DeliveryAddress.AdditionalInfo.Comment", "DeliveryLocation.OtherInformation.Comment");
		LinkedHashMap result = UniversalConverter.convert(orderModel, mapping);
		Assert.assertEquals(EMAIL,
				((LinkedHashMap) ((LinkedHashMap) result.get("DeliveryLocation")).get("OtherInformation")).get("Email"));
		Assert.assertEquals(COMMENT,
				((LinkedHashMap) ((LinkedHashMap) result.get("DeliveryLocation")).get("OtherInformation")).get("Comment"));
	}

	@Test
	public void testConvertHierarchy3Level_success()
	{
		mapping.put("DeliveryAddress.AdditionalInfo.PersonalInfo.Weight", "Person.Weight");
		LinkedHashMap result = UniversalConverter.convert(orderModel, mapping);
		Assert.assertEquals(WEIGHT,
				((LinkedHashMap) result.get("Person")).get("Weight"));
	}


}
