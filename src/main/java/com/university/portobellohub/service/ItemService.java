package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.ClothingItemCreateRequest;
import com.university.portobellohub.dto.request.ElectronicItemCreateRequest;
import com.university.portobellohub.dto.request.ItemReviewActionRequest;
import com.university.portobellohub.dto.request.ItemUpdateRequest;
import com.university.portobellohub.dto.response.ItemResponse;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.entity.Category;
import com.university.portobellohub.entity.ClothingItem;
import com.university.portobellohub.entity.ElectronicItem;
import com.university.portobellohub.entity.Item;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.ClothingSize;
import com.university.portobellohub.entity.enums.Gender;
import com.university.portobellohub.entity.enums.ItemCondition;
import com.university.portobellohub.entity.enums.ItemStatus;
import com.university.portobellohub.entity.enums.ItemType;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ForbiddenException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.ClothingItemRepository;
import com.university.portobellohub.repository.ElectronicItemRepository;
import com.university.portobellohub.repository.ItemRepository;
import com.university.portobellohub.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ElectronicItemRepository electronicItemRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final SecurityUtils securityUtils;
    private final ReviewRepository reviewRepository;

    public ItemService(
            ItemRepository itemRepository,
            ElectronicItemRepository electronicItemRepository,
            ClothingItemRepository clothingItemRepository,
            CategoryService categoryService,
            CloudinaryService cloudinaryService,
            SecurityUtils securityUtils,
            ReviewRepository reviewRepository
    ) {
        this.itemRepository = itemRepository;
        this.electronicItemRepository = electronicItemRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
        this.securityUtils = securityUtils;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> searchPublishedItems(
            String name,
            Long categoryId,
            ItemCondition condition,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        // Cerco gli oggetti pubblicati con i filtri
        Page<Item> items = itemRepository.searchPublishedItems(
                ItemStatus.PUBLISHED, name, categoryId, condition, minPrice, maxPrice, pageable
        );
        Page<ItemResponse> page = items.map(this::toItemResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getPendingItems(Pageable pageable) {
        return PageResponse.from(itemRepository.findByStatus(ItemStatus.PENDING_REVIEW, pageable)
                .map(this::toItemResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getMyItems(Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        return PageResponse.from(itemRepository.findBySellerId(currentUser.getId(), pageable)
                .map(this::toItemResponse));
    }

    @Transactional(readOnly = true)
    public ItemResponse getById(Long id) {
        return toItemResponse(findItem(id));
    }

    @Transactional
    public ItemResponse createElectronicItem(ElectronicItemCreateRequest request) {
        Category category = validateCategory(request.getCategoryId(), ItemType.ELECTRONIC);
        User seller = securityUtils.getCurrentUser();

        ElectronicItem item = new ElectronicItem();
        applyCommonFields(item, request.getName(), request.getDescription(), request.getPrice(),
                request.getCondition(), category, seller);
        item.setBrand(request.getBrand());
        item.setModel(request.getModel());
        item.setWarrantyMonths(request.getWarrantyMonths());
        item.setBatteryHealth(request.getBatteryHealth());

        return toItemResponse(electronicItemRepository.save(item));
    }

    @Transactional
    public ItemResponse createClothingItem(ClothingItemCreateRequest request) {
        Category category = validateCategory(request.getCategoryId(), ItemType.CLOTHING);
        User seller = securityUtils.getCurrentUser();

        ClothingItem item = new ClothingItem();
        applyCommonFields(item, request.getName(), request.getDescription(), request.getPrice(),
                request.getCondition(), category, seller);
        item.setSize(request.getSize());
        item.setMaterial(request.getMaterial());
        item.setGender(request.getGender());

        return toItemResponse(clothingItemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateElectronicItem(Long id, ItemUpdateRequest request) {
        // Cerco l'oggetto elettronico, se non esiste lancio eccezione
        ElectronicItem item = electronicItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Electronic item not found with id: " + id));
        
        // Verifico i permessi
        assertCanModify(item);
        applyUpdate(item, request);
        
        // Aggiorno i campi specifici
        if (request.getBrand() != null) {
            item.setBrand(request.getBrand());
        }
        if (request.getModel() != null) {
            item.setModel(request.getModel());
        }
        if (request.getWarrantyMonths() != null) {
            item.setWarrantyMonths(request.getWarrantyMonths());
        }
        if (request.getBatteryHealth() != null) {
            item.setBatteryHealth(request.getBatteryHealth());
        }
        
        resetReviewIfNeeded(item);
        
        // Salvo e ritorno la risposta
        return toItemResponse(electronicItemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateClothingItem(Long id, ItemUpdateRequest request) {
        ClothingItem item = clothingItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clothing item not found with id: " + id));
        assertCanModify(item);
        applyUpdate(item, request);
        if (request.getSize() != null) {
            item.setSize(ClothingSize.valueOf(request.getSize()));
        }
        if (request.getMaterial() != null) {
            item.setMaterial(request.getMaterial());
        }
        if (request.getGender() != null) {
            item.setGender(Gender.valueOf(request.getGender()));
        }
        resetReviewIfNeeded(item);
        return toItemResponse(clothingItemRepository.save(item));
    }

    @Transactional
    public ItemResponse uploadImage(Long id, MultipartFile file) {
        Item item = findItem(id);
        assertCanModify(item);

        cloudinaryService.deleteImage(item.getImagePublicId());
        CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(file, "portobello-hub/items");
        item.setImageUrl(uploadResult.url());
        item.setImagePublicId(uploadResult.publicId());

        return toItemResponse(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse reviewItem(Long id, ItemReviewActionRequest request) {
        Item item = findItem(id);
        User reviewer = securityUtils.getCurrentUser();

        if (request.getAction() != ItemStatus.PUBLISHED && request.getAction() != ItemStatus.REJECTED) {
            throw new BadRequestException("Action must be PUBLISHED or REJECTED");
        }
        if (item.getStatus() != ItemStatus.PENDING_REVIEW) {
            throw new BadRequestException("Only pending items can be reviewed");
        }
        if (request.getAction() == ItemStatus.REJECTED
                && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new BadRequestException("Rejection reason is required");
        }

        item.setStatus(request.getAction());
        item.setReviewedBy(reviewer);
        item.setReviewedAt(LocalDateTime.now());
        item.setRejectionReason(request.getAction() == ItemStatus.REJECTED ? request.getRejectionReason() : null);

        return toItemResponse(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = findItem(id);
        assertCanModify(item);
        if (item.getStatus() == ItemStatus.SOLD) {
            throw new BadRequestException("Sold items cannot be deleted");
        }
        cloudinaryService.deleteImage(item.getImagePublicId());
        itemRepository.delete(item);
    }

    public Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    private void applyCommonFields(
            Item item,
            String name,
            String description,
            BigDecimal price,
            ItemCondition condition,
            Category category,
            User seller
    ) {
        item.setSku(generateSku());
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCondition(condition);
        item.setCategory(category);
        item.setSeller(seller);
        item.setStatus(ItemStatus.PENDING_REVIEW);
    }

    private void applyUpdate(Item item, ItemUpdateRequest request) {
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getCondition() != null) {
            item.setCondition(request.getCondition());
        }
        if (request.getCategoryId() != null) {
            ItemType expectedType = item instanceof ElectronicItem ? ItemType.ELECTRONIC : ItemType.CLOTHING;
            item.setCategory(validateCategory(request.getCategoryId(), expectedType));
        }
    }

    private void resetReviewIfNeeded(Item item) {
        if (item.getStatus() == ItemStatus.PUBLISHED || item.getStatus() == ItemStatus.REJECTED) {
            item.setStatus(ItemStatus.PENDING_REVIEW);
            item.setReviewedBy(null);
            item.setReviewedAt(null);
            item.setRejectionReason(null);
        }
    }

    private Category validateCategory(Long categoryId, ItemType expectedType) {
        Category category = categoryService.findCategory(categoryId);
        if (category.getItemType() != expectedType) {
            throw new BadRequestException("Category type does not match item type");
        }
        return category;
    }

    private void assertCanModify(Item item) {
        User currentUser = securityUtils.getCurrentUser();
        boolean isOwner = item.getSeller().getId().equals(currentUser.getId());
        
        // Controllo se l'utente e' staff o admin tramite ciclo for invece di stream
        boolean isStaff = false;
        for (com.university.portobellohub.entity.Role role : currentUser.getRoles()) {
            if (role.getName() == RoleName.ROLE_STAFF || role.getName() == RoleName.ROLE_ADMIN) {
                isStaff = true;
                break;
            }
        }

        if (!isOwner && !isStaff) {
            throw new ForbiddenException("You are not allowed to modify this item");
        }
        if (item.getStatus() == ItemStatus.SOLD) {
            throw new BadRequestException("Sold items cannot be modified");
        }
    }

    private String generateSku() {
        return "SH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ItemResponse toItemResponse(Item item) {
        ItemResponse response = ItemResponse.fromEntity(item);
        response.setAverageRating(reviewRepository.findAverageRatingByItemId(item.getId()));
        response.setReviewCount(reviewRepository.countByItemId(item.getId()));
        return response;
    }
}
