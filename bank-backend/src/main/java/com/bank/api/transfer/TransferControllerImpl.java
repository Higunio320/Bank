package com.bank.api.transfer;

import com.bank.api.transfer.data.TransferListResponse;
import com.bank.api.transfer.data.TransferRequest;
import com.bank.api.transfer.data.TransferResponse;
import com.bank.api.transfer.interfaces.TransferController;
import com.bank.api.transfer.interfaces.TransferService;
import com.bank.entities.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.bank.api.transfer.constants.TransferControllerConstants.CREATE_TRANSFER_MAPPING;
import static com.bank.api.transfer.constants.TransferControllerConstants.GET_TRANSFERS_ON_PAGE_MAPPING;
import static com.bank.api.transfer.constants.TransferControllerConstants.TRANSFER_API_MAPPING;

@RestController
@RequestMapping(TRANSFER_API_MAPPING)
@RequiredArgsConstructor
public class TransferControllerImpl implements TransferController {

    private final TransferService transferService;

    @Override
    @GetMapping(GET_TRANSFERS_ON_PAGE_MAPPING)
    public ResponseEntity<TransferListResponse> getTransfersForUserOnPage(
            @AuthenticationPrincipal User user,
            @RequestParam("pageNumber") int pageNumber) {
        return ResponseEntity.ok(transferService.getTransfersForUserOnPage(user, pageNumber));
    }

    @Override
    @PostMapping(CREATE_TRANSFER_MAPPING)
    public ResponseEntity<TransferResponse> makeATransfer(
            @AuthenticationPrincipal User user,
            @RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(transferService.makeATransfer(user, transferRequest));
    }
}
