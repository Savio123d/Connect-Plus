package conne.connect.connect.Feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackResumoDTO {

    private Long positivos;
    private Long medianos;
    private Long negativos;
}
