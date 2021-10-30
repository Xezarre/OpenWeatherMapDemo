package dataobjects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ObjectInfo {

    private String status;
    private String statusDetail;
    private String resolutionType;
    private String resolutionAgent;
    private String processType;
    private String shippingMethod;
    private String shippingStatus;
    private String shippedTo;
    private String shippedFrom;
    private String resolutionTypeChangeReason;
}
