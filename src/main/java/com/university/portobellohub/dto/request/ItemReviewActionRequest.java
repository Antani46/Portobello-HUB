package com.university.portobellohub.dto.request;

import com.university.portobellohub.entity.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ItemReviewActionRequest {

    @NotNull
    private ItemStatus action;

    @Size(max = 500)
    private String rejectionReason;

    public ItemStatus getAction() {
        return action;
    }

    public void setAction(ItemStatus action) {
        this.action = action;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
