package com.ems.application.controller.table;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.table.NewTableRequest;
import com.ems.application.dto.table.TableListSearchCriteria;
import com.ems.application.dto.table.TableResponse;
import com.ems.application.service.table.TableService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Table")

@RestController
@RequestMapping(value = "/api/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @ApiOperation(value = "Add new table")
    @PostMapping(value = "/add")
    public ResponseEntity<TableResponse> addTable(@Valid @RequestBody NewTableRequest tableRequest) {
        return tableService.createNewTable(tableRequest);
    }

    @ApiOperation(value = "Get list table ")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<TableResponse>> getCategories(
            @Valid @RequestBody TableListSearchCriteria criteria) {
        return tableService.getAllTable(criteria);
    }

    @ApiOperation(value = "Get table by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<TableResponse> getTable(@PathVariable("id") String id) {
        return tableService.getTableById(id);
    }

    @ApiOperation(value = "Update table")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<TableResponse> updateTable(@PathVariable("id") String id,
            @Valid @RequestBody NewTableRequest tableRequest) {
        return tableService.updateTable(id, tableRequest);
    }

    @ApiOperation(value = "Delete table")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<TableResponse> deleteTable(@PathVariable("id") String id) {
        return tableService.deleteTable(id);
    }
}
