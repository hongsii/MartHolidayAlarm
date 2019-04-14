package com.hongsi.martholidayalarm.api.controller;

import com.hongsi.martholidayalarm.api.converter.MartTypeParameterConverter;
import com.hongsi.martholidayalarm.domain.mart.MartOrder.Property;
import com.hongsi.martholidayalarm.domain.mart.MartType;
import com.hongsi.martholidayalarm.service.MartService;
import com.hongsi.martholidayalarm.service.dto.mart.MartDto;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
@RestController
@RequestMapping("/api/mart")
@AllArgsConstructor
@Api(description = "마트 정보 API", tags = {"마트"})
@ApiResponses(value = {
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad Request - invalid parameters")
})
public class MartController {

	private final MartService martService;

	@ApiOperation(value = "특정 마트의 지점 조회")
	@ApiImplicitParam(name = "mart_type", value = "마트타입 (대소문자 구분하지 않음)", required = true, dataType = "string", paramType = "path")
	@GetMapping(value = "/{mart_type}/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMarts(
			@PathVariable("mart_type") @Valid MartType martType) {
		List<MartDto.Response> marts = martService
				.findAllByMartType(martType, defaultSort());
		if (martType == MartType.EMART) {
			marts.addAll(martService
					.findAllByMartType(MartType.EMART_TRADERS, defaultSort()));
		}
		return new ResponseEntity<>(marts.stream()
				.map(MartDto.Response::toTempResponse)
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	@ApiOperation(value = "지점ID로 조회")
	@ApiImplicitParam(name = "ids", value = "지점ID (한 개 또는 그 이상)", required = true, paramType = "path", allowMultiple = true)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "There is no exist mart for id")
	})
	@GetMapping(value = "/branch/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBranches(
			@PathVariable("ids") @Valid Set<Long> ids) {
		List<MartDto.Response> marts = martService.findAllById(ids, defaultSort());
		return new ResponseEntity<>(marts.stream()
				.map(MartDto.Response::toTempResponse)
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	private Sort defaultSort() {
		return Sort.by(Property.martType.asc(), Property.branchName.asc());
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(MartType.class, new MartTypeParameterConverter());
	}
}
