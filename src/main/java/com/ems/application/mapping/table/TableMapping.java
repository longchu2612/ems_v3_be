package com.ems.application.mapping.table;

import com.ems.application.dto.table.NewTableRequest;
import com.ems.application.dto.table.TableResponse;
import com.ems.application.entity.Tables;
import com.ems.application.util.HashIdsUtils;

public class TableMapping {
    public static Tables convertToEntity(NewTableRequest tableRequest, Tables dtbTable) {
        dtbTable.setName(tableRequest.getName());
        dtbTable.setPosition(tableRequest.getPosition());
        dtbTable.setStatus(tableRequest.getStatus());
        return dtbTable;
    }

    public static TableResponse convertToDto(Tables dtbTable, HashIdsUtils hashIdsUtils) {
        TableResponse response = new TableResponse();
        response.setId(hashIdsUtils.encodeId(dtbTable.getId()));
        response.setPosition(dtbTable.getPosition());
        response.setName(dtbTable.getName());
        response.setStatus(dtbTable.getStatus());
        return response;
    }

}
