package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

	private Long id;

	// * Header Info
	private String company;
	private String poNo;
	private String vendor;
	private LocalDate poDate;
	private LocalDate deliveryDate;
	private String paymentTerm;
	private String note;

	// * Totals
	private BigDecimal subTotal;
	private BigDecimal vatAmount;
	private BigDecimal totalAmount;

	// * Items
	@Builder.Default
	private List<PurchaseOrderItem> items = new ArrayList<>();
}
