package com.udaya.service;

import com.udaya.exception.GlobalException;
import com.udaya.model.PurchaseOrder;
import com.udaya.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

	private final PurchaseOrderRepository poRepository;

	// * Get All with Pagination
	public Page<PurchaseOrder> getAll(Pageable pageable, String keyword) {
		return poRepository.findAll(pageable, keyword);
	}

	// * Get All (Legacy/Internal)
	public List<PurchaseOrder> getAll() {
		return poRepository.findAll();
	}

	// * Get By ID
	public PurchaseOrder getById(Long id) {
		return poRepository.findById(id).orElseThrow(() -> new GlobalException("Purchase Order not found", 404));
	}

	// * Create
	@Transactional
	public PurchaseOrder create(PurchaseOrder po) {
		if (po.getPoDate() == null) po.setPoDate(LocalDate.now());

		// * Items are handled by Repository (JOOQ)

		return poRepository.save(po);
	}

	// * Update
	@Transactional
	public PurchaseOrder update(Long id, PurchaseOrder request) {
		PurchaseOrder po = getById(id);

		po.setCompany(request.getCompany());
		po.setPoNo(request.getPoNo()); // Added missing field update if needed
		po.setVendor(request.getVendor());
		po.setDeliveryDate(request.getDeliveryDate());
		po.setPaymentTerm(request.getPaymentTerm());
		po.setNote(request.getNote());

		po.setSubTotal(request.getSubTotal());
		po.setVatAmount(request.getVatAmount());
		po.setTotalAmount(request.getTotalAmount());

		// * Update Items
		po.setItems(request.getItems());
		// * Ensure ID matches for update if needed, but Repository usually handles it based on Parent ID

		// * Set ID to ensure it updates the correct record
		po.setId(id);

		return poRepository.save(po);
	}

	// * Delete
	public void delete(Long id) {
		if (!poRepository.existsById(id)) throw new GlobalException("Purchase Order not found", 404);
		poRepository.deleteById(id);
	}
}
