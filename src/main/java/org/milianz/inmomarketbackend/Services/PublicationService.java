package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.*;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationDefaultDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Repositories.*;
import org.milianz.inmomarketbackend.Utils.PublicationsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PublicationService {

    @Autowired
    private iPublicationRepository publicationRepository;
    @Autowired
    private iUserRepository userRepository;
    @Autowired
    private PropertyTypeService propertyTypeService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private iPropertyTypeRepository propertyTypeRepository;
    @Autowired
    private iLocationRepository locationRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private AvailableTimeService availableTimeService;
    @Autowired
    private iFavoriteRepository favoriteRepository;

    public ResponseEntity<?> createPublication(@RequestBody PublicationSaveDTO publicationSaveDTO, String userName, MultipartFile[] files) {
        try {
            User user = userRepository.findByEmail(userName)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userName));

            Publication publication = new Publication();
            publication.setUser(user);
            publication.setPropertyType(propertyTypeService.createPropertyType(publicationSaveDTO));
            publication.setLocation(locationService.createLocation(publicationSaveDTO));
            publication.setPropertyAddress(publicationSaveDTO.getPropertyAddress());
            publication.setPropertyTitle(publicationSaveDTO.getPropertyTitle());
            publication.setLongitude(publicationSaveDTO.getLongitude());
            publication.setLatitude(publicationSaveDTO.getLatitude());
            publication.setPropertySize(publicationSaveDTO.getPropertySize());
            publication.setPropertyBedrooms(publicationSaveDTO.getPropertyBedrooms());
            publication.setPropertyFloors(publicationSaveDTO.getPropertyFloors());
            publication.setPropertyParking(publicationSaveDTO.getPropertyParking());
            publication.setPropertyFurnished(publicationSaveDTO.getPropertyFurnished());
            publication.setPropertyDescription(publicationSaveDTO.getPropertyDescription());
            publication.setPropertyPrice(publicationSaveDTO.getPropertyPrice());
            publication.setCreatedAt(LocalDateTime.now());
            publication.setUpdatedAt(LocalDateTime.now());
            publication.setStatus(Publication.PublicationStatus.ACTIVE);

            publicationRepository.save(publication);

            List<PropertyImage> propertyImage = cloudinaryService.uploadImage(files, publication);
            publication.setPropertyImages(propertyImage);

            List<AvailableTime> availableTimes = availableTimeService.createAvailableTime(publicationSaveDTO, publication);
            publication.setAvailableTimes(availableTimes);

            publicationRepository.save(publication);

            PublicationsConstructor constructor = new PublicationsConstructor();

            return ResponseEntity.ok(constructor.PublicationUnique(publication));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating publication: " + e.getMessage());
        }
    }

    public List<PublicationDefaultDTO> getAllPublications() {
        List<Publication> publications = publicationRepository.findByStatus(Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByDepartment(String department) {
        List<Publication> publications = publicationRepository.findByLocation_Department(department);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Publication> publications = publicationRepository.findByPropertyPriceBetween(minPrice, maxPrice);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByType(String typeName) {
        List<Publication> publications = publicationRepository.findByPropertyType_TypeName(typeName);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsBySize(BigDecimal minSize, BigDecimal maxSize) {
        List<Publication> publications = publicationRepository.findByPropertySizeBetween(minSize, maxSize);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByBedrooms(Integer bedrooms) {
        List<Publication> publications = publicationRepository.findByPropertyBedrooms(bedrooms);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByFloors(Integer floors) {
        List<Publication> publications = publicationRepository.findByPropertyFloors(floors);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByParking(Integer parking) {
        List<Publication> publications = publicationRepository.findByPropertyParking(parking);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getPublicationsByFurnished(Boolean furnished) {
        List<Publication> publications = publicationRepository.findByPropertyFurnished(furnished);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getAllActivePublications() {
        List<Publication> publications = publicationRepository.findByStatus(Publication.PublicationStatus.ACTIVE);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getAllPublicationsbyUserId(UUID userId) {
        List<Publication> publications = publicationRepository.findByUser_Id(userId);
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public PublicationDefaultDTO getPublicationById(UUID publicationId) {
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationUnique(publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found with ID: " + publicationId)));
    }

    public List<PublicationDefaultDTO> getLastPublications() {
        List<Publication> publications = publicationRepository.findTop10ByOrderByCreatedAtDesc();
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }

    public List<PublicationDefaultDTO> getTop10MostPopularPublications() {
        List<Favorite> favorites = favoriteRepository.findTop10ByOrderByPublicationIdDesc();
        List<Publication> publications = favorites.stream()
                .map(favorite -> favorite.getPublication())
                .toList();
        PublicationsConstructor constructor = new PublicationsConstructor();
        return constructor.PublicationsList(publications);
    }
}