package com.udaya.controller;

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
@RequestMapping("/purchase-orders") // * Base path via application.yaml
@RequiredArgsConstructor
@Tag(name = "Purchase Order", description = "Purchase Order Management")
@SecurityRequirement(name = "BearerAuth")
public class PurchaseOrderController {

	private final PurchaseOrderService poService;

	@GetMapping
	@Operation(summary = "Get All POs with Pagination")
	@RequiresPermission(module = "PURCHASE_ORDER", action = "VIEW")
	public ResponseEntity<BaseResponse<Object>> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id")
		                                                       .descending());
		Page<PurchaseOrder> poPage = poService.getAll(pageable);

		Pagination pagination = Pagination.builder()
		                                  .page(page)
		                                  .rowsPerPage(size)
		                                  .total(poPage.getTotalElements())
		                                  .build();

		return ResponseEntity.ok(BaseResponse.success(poPage.getContent(), pagination));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get PO by ID")
	@RequiresPermission(module = "PURCHASE_ORDER", action = "VIEW")
	public ResponseEntity<BaseResponse<PurchaseOrder>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(BaseResponse.success(poService.getById(id)));
	}

	@PostMapping
	@Operation(summary = "Create PO")
	@RequiresPermission(module = "PURCHASE_ORDER", action = "CREATE")
	public ResponseEntity<BaseResponse<PurchaseOrder>> create(@RequestBody PurchaseOrder po) {
		return ResponseEntity.ok(BaseResponse.success(poService.create(po)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update PO")
	@RequiresPermission(module = "PURCHASE_ORDER", action = "EDIT")
	public ResponseEntity<BaseResponse<PurchaseOrder>> update(@PathVariable Long id, @RequestBody PurchaseOrder po) {
		return ResponseEntity.ok(BaseResponse.success(poService.update(id, po)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete PO")
	@RequiresPermission(module = "PURCHASE_ORDER", action = "DELETE")
	public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
		poService.delete(id);
		return ResponseEntity.ok(BaseResponse.success(null));
	}
}
