package com.hongsi.martholidayalarm.mart.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Test;

public class MartTest {

	@Test
	public void update() {
		Mart savedMart = Mart.builder()
				.id(1L)
				.martType(MartType.EMART)
				.realId("1000")
				.region("서울")
				.branchName("성수점")
				.address("서울 성동구 뚝섬로 379")
				.phoneNumber("02-3408-1234")
				.url("http://store.emart.com/branch/list.do?id=1038")
				.holidays(asList(Holiday.builder()
						.date(LocalDate.of(2018, 6, 20)).build()))
				.build();

		Mart martToUpdate = Mart.builder()
				.martType(MartType.EMART)
				.realId("1000")
				.region("서울")
				.branchName("성수점")
				.address("서울 성동구 뚝섬로 379")
				.phoneNumber("02-3408-1234")
				.url("http://store.emart.com/branch/list.do?id=1038")
				.holidays(asList(Holiday.builder()
								.date(LocalDate.of(2018, 6, 20)).build(),
						Holiday.builder()
								.date(LocalDate.of(2018, 6, 22)).build()
				))
				.build();

		martToUpdate.update(savedMart);

		assertThat(martToUpdate.getHolidays().size()).isEqualTo(1);
		assertThat(martToUpdate.getHolidays().get(0)).isEqualTo(Holiday.builder()
				.date(LocalDate.of(2018, 6, 22))
				.build());
	}
}