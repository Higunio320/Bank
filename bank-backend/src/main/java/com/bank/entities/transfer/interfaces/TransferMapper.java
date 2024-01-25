package com.bank.entities.transfer.interfaces;

import com.bank.api.transfer.data.TransferResponse;
import com.bank.entities.transfer.Transfer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    TransferResponse transferToTransferResponse(Transfer transfer);
}
