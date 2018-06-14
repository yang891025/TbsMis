package com.tbs.chat.entity;

import java.io.Serializable;

public class CountryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public CountryEntity() {

	}

	public CountryEntity(String country, String number,String country_initial, String data1, String data2, String data3, String data4, String data5) {
		super();
		this.country = country;
		this.number = number;
		this.country_initial = country_initial;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
		
	}

	private String country_initial;
	private String country;
	private String number;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;


	public String getCountry_initial() {
		return country_initial;
	}

	public void setCountry_initial(String country_initial) {
		this.country_initial = country_initial;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}
	
}
