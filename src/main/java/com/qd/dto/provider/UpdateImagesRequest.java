package com.qd.dto.provider;

import java.util.List;

public class UpdateImagesRequest {
    private List<Long> retainImageIds; 

    public List<Long> getRetainImageIds() { 
        return retainImageIds; 
    }
    public void setRetainImageIds(List<Long> retainImageIds) {
        this.retainImageIds = retainImageIds; 
    }
}
