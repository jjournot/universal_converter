package com.jeromejou.uniconv;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Test service which calculate 20% of taxes on a given amount
 */
@RestController
public class ConversionController
{
	private final Logger LOG = LoggerFactory.getLogger(ConversionController.class);

	@RequestMapping("/conversion")
	public ResponseEntity<Object> convert()
	{
		LOG.debug("======= Convert ");
		return new ResponseEntity<Object>(new Object(), HttpStatus.OK);
	}
}
