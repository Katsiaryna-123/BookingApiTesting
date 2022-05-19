package Beans;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenCredentialsModel {
    private String username;
    private String password;
}
