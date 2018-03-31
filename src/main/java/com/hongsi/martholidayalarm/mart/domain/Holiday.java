package com.hongsi.martholidayalarm.mart.domain;

import java.time.LocalDate;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Holiday extends BaseEntity {

	@EmbeddedId
	private HolidayId id;

	@MapsId("martId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mart_id", referencedColumnName = "id")
	private Mart mart;

	@Builder
	public Holiday(LocalDate date, Mart mart) {
		this.mart = mart;
		this.id = HolidayId.builder()
				.date(date)
				.martId(mart.getId())
				.build();
	}

	public LocalDate getHoliday() {
		return this.id.getDate();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Holiday holiday = (Holiday) o;

		if (!id.equals(holiday.id)) {
			return false;
		}
		return mart.equals(holiday.mart);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + mart.hashCode();
		return result;
	}
}