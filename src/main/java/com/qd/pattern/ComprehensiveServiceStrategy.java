/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.pattern;

import com.qd.dto.provider.BaseComprehensiveRequest;
import com.qd.dto.provider.UpdateSubItemRequest;
import com.qd.pojo.Services;

/**
 *
 * @author ADMIN
 */
public interface ComprehensiveServiceStrategy {
    void saveDetails(Services service, BaseComprehensiveRequest req);
    void updateDetails(Services service, BaseComprehensiveRequest req);
    void addSubItem(Services service, BaseComprehensiveRequest req);
    void updateSubItem(Services service, Long subItemId, UpdateSubItemRequest req);
}
