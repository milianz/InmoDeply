package org.milianz.inmomarketbackend.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "El número de teléfono debe tener un formato válido")
    private String phoneNumber;

    // Para subir nueva foto de perfil
    private MultipartFile profilePicture;

    // Para eliminar foto de perfil existente
    private Boolean removeProfilePicture = false;
}