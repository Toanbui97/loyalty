package vn.com.loyalty.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.MasterDataRequest;
import vn.com.loyalty.core.dto.response.cms.MasterDataResponse;
import vn.com.loyalty.core.service.internal.MasterDataService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService masterDataService;
    private final ResponseFactory responseFactory;

    @PostMapping("/performCreateOrUpdateMasterData")
    public ResponseEntity<BodyResponse<MasterDataResponse>> performCreateOrUpdateMasterData(@RequestBody BodyRequest<MasterDataRequest> request) {
        return responseFactory.success(masterDataService.setValue(request.getData()));
    }



}
