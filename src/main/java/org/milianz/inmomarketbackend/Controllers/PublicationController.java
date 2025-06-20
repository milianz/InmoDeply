package org.milianz.inmomarketbackend.Controllers;


import jakarta.validation.Valid;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Services.CloudinaryService;
import org.milianz.inmomarketbackend.Services.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity <?> createPublication(@Valid @ModelAttribute PublicationSaveDTO publicationSaveDTO, Principal principal, @RequestParam("files") MultipartFile[] files) throws Exception {
        return publicationService.createPublication(publicationSaveDTO , principal.getName(), files);
    }

    @GetMapping("All")
    public ResponseEntity<List<PublicationDefaultDTO>> getAllPublications() {
        try {
            return ResponseEntity.ok(publicationService.getAllPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<PublicationDefaultDTO>> getPublicationsFilters(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String minSize,
            @RequestParam(required = false) String maxSize,
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String floors,
            @RequestParam(required = false) String parking,
            @RequestParam(required = false) String furnished
    ) {
        if (department != null) {
        return ResponseEntity.ok(publicationService.getPublicationsByDepartment(department));
        } else if (minPrice != null && maxPrice != null) {
            BigDecimal BDmaxPrice = new BigDecimal(maxPrice);
            BigDecimal BDminPrice = new BigDecimal(minPrice);
            return ResponseEntity.ok(publicationService.getPublicationsByPrice(BDminPrice, BDmaxPrice));
        } else if (typeName != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByType(typeName));
        } else if (minSize != null && maxSize != null) {
            BigDecimal BDmaxSize = new BigDecimal(maxSize);
            BigDecimal BDminSize = new BigDecimal(minSize);
            return ResponseEntity.ok(publicationService.getPublicationsBySize(BDminSize, BDmaxSize));
        } else if (bedrooms != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByBedrooms(Integer.parseInt(bedrooms)));
        } else if (floors != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByFloors(Integer.parseInt(floors)));
        } else if (parking != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByParking(Integer.parseInt(parking)));
        } else if (furnished != null) {
            return ResponseEntity.ok(publicationService.getPublicationsByFurnished(Boolean.parseBoolean(furnished)));
        }else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/userPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getUserPublications( @RequestParam("userID") UUID userID) {
        try {
            return ResponseEntity.ok(publicationService.getAllPublicationsbyUserId(userID));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/publicationById")
    public ResponseEntity<PublicationDefaultDTO> getPublicationById(@RequestParam("publicationId") String publicationId) {
        UUID publication = UUID.fromString(publicationId);
        try {
            return ResponseEntity.ok(publicationService.getPublicationById(publication));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/lastPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getLastPublications() {
        try {
            return ResponseEntity.ok(publicationService.getLastPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/mostPopularPublications")
    public ResponseEntity<List<PublicationDefaultDTO>> getMostPopularPublications() {
        try {
            return ResponseEntity.ok(publicationService.getTop10MostPopularPublications());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
