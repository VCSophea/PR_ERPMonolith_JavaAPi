package com.udaya.controller;

import com.udaya.dto.common.BaseSearchRequest;
import com.udaya.model.PurchaseOrder;
import com.udaya.response.BaseResponse;
import com.udaya.response.Pagination;
import com.udaya.security.RequiresPermission;
import com.udaya.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "5. Purchase Order")
@SecurityRequirement(name = "BearerAuth")
public class PurchaseOrderController {

	private final PurchaseOrderService poService;

	@PostMapping("/list")
	@Operation(summary = "Get All POs with Pagination")
	@RequiresPermission(module = "Purchase Order", action = "View")
	public ResponseEntity<BaseResponse<Object>> getAll(@RequestBody BaseSearchRequest request) {

		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by("id").descending());
		Page<PurchaseOrder> poPage = poService.getAll(pageable, request.getKeyword());

		Pagination pagination = Pagination.builder().page(request.getPage()).rowsPerPage(request.getSize()).total(poPage.getTotalElements()).build();

		return ResponseEntity.ok(BaseResponse.success(poPage.getContent(), pagination));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get PO by ID")
	@RequiresPermission(module = "Purchase Order", action = "View")
	public ResponseEntity<BaseResponse<PurchaseOrder>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(BaseResponse.success(poService.getById(id)));
	}

	@PostMapping
	@Operation(summary = "Create PO")
	@RequiresPermission(module = "Purchase Order", action = "Add")
	public ResponseEntity<BaseResponse<PurchaseOrder>> create(@RequestBody PurchaseOrder po) {
		return ResponseEntity.ok(BaseResponse.success(poService.create(po)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update PO")
	@RequiresPermission(module = "Purchase Order", action = "Edit")
	public ResponseEntity<BaseResponse<PurchaseOrder>> update(@PathVariable Long id, @RequestBody PurchaseOrder po) {
		return ResponseEntity.ok(BaseResponse.success(poService.update(id, po)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete PO")
	@RequiresPermission(module = "Purchase Order", action = "Delete")
	public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
		poService.delete(id);
		return ResponseEntity.ok(BaseResponse.success(null));
	}
}
