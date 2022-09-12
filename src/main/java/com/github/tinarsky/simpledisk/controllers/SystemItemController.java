package com.github.tinarsky.simpledisk.controllers;

import com.github.tinarsky.simpledisk.domain.SystemItem;
import com.github.tinarsky.simpledisk.exceptions.BadRequestException;
import com.github.tinarsky.simpledisk.models.SystemItemImportRequest;
import com.github.tinarsky.simpledisk.services.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestController
public class SystemItemController {
	private final SystemItemService systemItemService;

	@Autowired
	public SystemItemController(SystemItemService systemItemService) {
		this.systemItemService = systemItemService;
	}

	@PostMapping("/imports")
	public void imports(@RequestBody SystemItemImportRequest itemImportRequest) {
		try {
			systemItemService.save(itemImportRequest);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(e.getConstraintViolations()
					.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", ")));
		}
	}

	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") String id,
					   @RequestParam("date") String date) {
		try {
			systemItemService.delete(id, date);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(e.getConstraintViolations()
					.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", ")));
		}
	}

	@GetMapping("/nodes/{id}")
	public SystemItem nodes(@PathVariable("id") String id){
		return systemItemService.getNodes(id);
	}
}
