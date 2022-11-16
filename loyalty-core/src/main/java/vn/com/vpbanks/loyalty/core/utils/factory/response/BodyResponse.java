package vn.com.vpbanks.loyalty.core.utils.factory.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import vn.com.vpbanks.loyalty.core.constant.enums.ResponseStatusCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vn.com.vpbanks.loyalty.core.utils.DateTimeUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BodyResponse<T> implements Serializable {


    private int status; // 1: Success, 0: Fail
    private String code;
    private String message;
    private String errorMessage;
    private String requestId;
    private Integer page;
    private Integer size;
    private Long totalRecord;
    private Integer totalPage;
    private T data;
    private List<T> dataList;


    @JsonFormat(pattern = DateTimeUtils.FORMAT_DATE_TIME3)
    private LocalDateTime responseTime;
    private List<String> details;


    public BodyResponse(int status, ResponseStatusCode responseStatusCode, String requestId) {
        this.status = status;
        this.code = responseStatusCode.getCode();
        this.message = responseStatusCode.getMessage();
        this.requestId = requestId;
    }


    public BodyResponse(int status, ResponseStatusCode responseStatusCode, String requestId, T data){
        this(status, responseStatusCode, requestId);
        this.setData(data);
    }

    public BodyResponse(int status, ResponseStatusCode responseStatusCode, String requestId, List<T> dataList){
        this(status, responseStatusCode, requestId);
        this.setDataList(dataList);
    }

    public BodyResponse(int status, ResponseStatusCode responseStatusCode, String requestId, PageImpl page){
        this(status, responseStatusCode, requestId);
        this.setDataList(page.getContent());
        this.setPage(page.getNumber());
        this.setSize(page.getSize());
        this.setTotalRecord(page.getTotalElements());
        this.setTotalPage(page.getTotalPages());
    }

    public BodyResponse(String code, String message, String requestId) {
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.responseTime = LocalDateTime.now();
        this.setCode(code);
    }

    public BodyResponse(String code, String message, String requestId, int status, T data) {
        this.data = data;
        this.code = code;
        this.status = status;
        this.message = message;
        this.requestId = requestId;
        this.responseTime = LocalDateTime.now();
        this.setCode(code);
    }

    public BodyResponse(ResponseStatusCode responseStatus, String requestId, int status, T data) {
        this.requestId = requestId;
        this.status = status;
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.responseTime = LocalDateTime.now();
        this.data = data;
        this.setCode(code);
    }

    public BodyResponse(ResponseStatusCode responseStatus, String requestId, List<T> dataList) {
        this.requestId = requestId;
        this.status = status;
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.responseTime = LocalDateTime.now();
        this.dataList = dataList;
        this.setCode(code);
    }

    public BodyResponse(String code, String message, String requestId, int status, List<T> dataList) {
        this.dataList = dataList;
        this.code = code;
        this.status = status;
        this.message = message;
        this.requestId = requestId;
        this.responseTime = LocalDateTime.now();
        this.setCode(code);
    }

    public BodyResponse(ResponseStatusCode responseStatus, String requestId, int status) {
        this.requestId = requestId;
        this.status = status;
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.responseTime = LocalDateTime.now();
        this.setCode(code);
    }

    public BodyResponse(ResponseStatusCode responseStatus, String requestId, int status, List<String> details) {
        this.requestId = requestId;
        this.status = status;
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.responseTime = LocalDateTime.now();
        this.details = details;
        this.setCode(code);
    }

    public BodyResponse(ResponseStatusCode responseStatus, String requestId, int status, String errorMessage) {
        this.requestId = requestId;
        this.status = status;
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
        this.errorMessage = errorMessage;
        this.responseTime = LocalDateTime.now();
        this.details = details;
        this.setCode(code);
    }


    private void setCode(String code) {
        this.code = code;
    }
}
