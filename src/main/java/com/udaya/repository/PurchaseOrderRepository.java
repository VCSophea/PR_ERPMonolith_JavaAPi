package com.udaya.repository;

import com.udaya.model.PurchaseOrder;
import com.udaya.model.PurchaseOrderItem;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepository {

	// * Tables
	private static final String TABLE_PO = "purchase_orders";
	private static final String TABLE_ITEMS = "purchase_order_items";
	private final DSLContext dsl;

	// * Find All with Pagination (Optimized 1+N)
	public Page<PurchaseOrder> findAll(Pageable pageable) {
		long total = dsl.fetchCount(table(TABLE_PO));

		List<PurchaseOrder> pos = dsl.selectFrom(table(TABLE_PO)).orderBy(field("id").desc()).limit(pageable.getPageSize()).offset(pageable.getOffset()).fetchInto(PurchaseOrder.class);

		// * Fetch Items Efficiently
		if (!pos.isEmpty()) {
			var poIds = pos.stream().map(PurchaseOrder::getId).toList();
			var itemsMap = dsl.selectFrom(table(TABLE_ITEMS)).where(field("purchase_order_id").in(poIds)).fetchInto(PurchaseOrderItem.class).stream().collect(Collectors.groupingBy(PurchaseOrderItem::getPurchaseOrderId));

			pos.forEach(po -> po.setItems(itemsMap.getOrDefault(po.getId(), List.of())));
		}

		return new PageImpl<>(pos, pageable, total);
	}

	// * Find All (List)
	public List<PurchaseOrder> findAll() {
		return dsl.selectFrom(table(TABLE_PO)).fetchInto(PurchaseOrder.class);
	}

	// * Find By ID
	public Optional<PurchaseOrder> findById(Long id) {
		return dsl.selectFrom(table(TABLE_PO)).where(field("id").eq(id)).fetchOptionalInto(PurchaseOrder.class).map(po -> {
			po.setItems(dsl.selectFrom(table(TABLE_ITEMS)).where(field("purchase_order_id").eq(id)).fetchInto(PurchaseOrderItem.class));
			return po;
		});
	}

	// * Save (Create/Update)
	public PurchaseOrder save(PurchaseOrder po) {
		Long poId = po.getId();

		// * Upsert Header
		if (poId == null) {
			poId = dsl.insertInto(table(TABLE_PO))
			          .set(field("company"), po.getCompany())
			          .set(field("po_no"), po.getPoNo())
			          .set(field("vendor"), po.getVendor())
			          .set(field("po_date"), po.getPoDate())
			          .set(field("delivery_date"), po.getDeliveryDate())
			          .set(field("payment_term"), po.getPaymentTerm())
			          .set(field("note"), po.getNote())
			          .set(field("sub_total"), po.getSubTotal())
			          .set(field("vat_amount"), po.getVatAmount())
			          .set(field("total_amount"), po.getTotalAmount())
			          .returning(field("id"))
			          .fetchOne("id", Long.class);
			po.setId(poId);
		} else {
			dsl.update(table(TABLE_PO))
			   .set(field("company"), po.getCompany())
			   .set(field("po_no"), po.getPoNo())
			   .set(field("vendor"), po.getVendor())
			   .set(field("po_date"), po.getPoDate())
			   .set(field("delivery_date"), po.getDeliveryDate())
			   .set(field("payment_term"), po.getPaymentTerm())
			   .set(field("note"), po.getNote())
			   .set(field("sub_total"), po.getSubTotal())
			   .set(field("vat_amount"), po.getVatAmount())
			   .set(field("total_amount"), po.getTotalAmount())
			   .where(field("id").eq(poId))
			   .execute();

			// * Clear existing items for update
			dsl.deleteFrom(table(TABLE_ITEMS)).where(field("purchase_order_id").eq(poId)).execute();
		}

		// * Batch Insert Items
		if (po.getItems() != null && !po.getItems().isEmpty()) {
			var insert = dsl.insertInto(table(TABLE_ITEMS), field("sku"), field("product_name"), field("quantity"), field("unit_cost"), field("total_cost"), field("purchase_order_id"));

			Long finalPoId = poId; // effective final
			po.getItems().forEach(item -> insert.values(item.getSku(), item.getProductName(), item.getQuantity(), item.getUnitCost(), item.getTotalCost(), finalPoId));

			insert.execute();
		}

		return po;
	}

	// * Delete
	public void deleteById(Long id) {
		dsl.deleteFrom(table(TABLE_ITEMS)).where(field("purchase_order_id").eq(id)).execute();
		dsl.deleteFrom(table(TABLE_PO)).where(field("id").eq(id)).execute();
	}

	// * Exists
	public boolean existsById(Long id) {
		return dsl.fetchCount(dsl.selectFrom(table(TABLE_PO)).where(field("id").eq(id))) > 0;
	}
}
