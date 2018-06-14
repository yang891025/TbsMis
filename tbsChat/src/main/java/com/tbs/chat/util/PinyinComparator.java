package com.tbs.chat.util;

import java.util.Comparator;

import com.tbs.chat.entity.CountryEntity;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		CountryEntity a1 = (CountryEntity) o1;
		CountryEntity a2 = (CountryEntity) o2;
		String str1 = PingYinUtil.getPingYin(a1.getCountry()).toLowerCase();
		String str2 = PingYinUtil.getPingYin(a2.getCountry()).toLowerCase();
		return str1.compareTo(str2);
	}

}
