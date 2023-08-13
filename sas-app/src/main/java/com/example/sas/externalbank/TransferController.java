package com.example.sas.externalbank;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "TransferToBankController", description = "Controller defines entry points to transfer to external Bank")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/api/banktransfer")
    public ResponseEntity<String> transferFunds(@RequestBody Transfer transfer) {
        transferService.transferFunds(transfer);
        return ResponseEntity.ok("Transfer request sent!");
    }

}