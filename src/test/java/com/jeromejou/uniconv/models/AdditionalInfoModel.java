package com.jeromejou.uniconv.models;

public class AdditionalInfoModel
{
	private String comment;
	private PersonalInfoModel personalInfo;

	public String getComment()
	{
		return comment;
	}

	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	public PersonalInfoModel getPersonalInfo()
	{
		return personalInfo;
	}

	public void setPersonalInfo(final PersonalInfoModel personalInfo)
	{
		this.personalInfo = personalInfo;
	}
}