package uz.pdp.word;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Word {
    private Long id;
    private Long userId;
    private String word;
    private String definition;
    private Date createdAt;
}
