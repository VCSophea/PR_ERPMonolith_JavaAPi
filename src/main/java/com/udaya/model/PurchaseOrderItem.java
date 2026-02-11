package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

	private Long id;

	// * Item Details
	private String sku;
	private String upc;
	private String productName;
	private String note;
	private Integer quantity; // Renamed from qty to quantity to match Repo usage in previous step
	private String uom;
	private BigDecimal unitCost;
	private BigDecimal totalCost;

	private Long purchaseOrderId; // FK
}
