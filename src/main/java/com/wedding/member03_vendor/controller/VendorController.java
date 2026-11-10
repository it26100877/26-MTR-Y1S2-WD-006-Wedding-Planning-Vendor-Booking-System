package com.wedding.member03_vendor.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.common.utils.FileUtil;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member06_review_feedback.service.ReviewService;
import com.wedding.member04_booking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * Controller managing Vendor registration, catalog browsing, filtering, and admin approvals.
 * Integrates with Member 06 for displaying dynamic ratings.
 *
 * @author Member 03 – Vendor Management
 */
@Controller
public class VendorController {

 private final VendorService vendorService;
 private final ReviewService reviewService;
 private final BookingService bookingService;
 private static final long MAX_VENDOR_IMAGE_BYTES = 5L * 1024 * 1024;
 private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
 private static final Map<VendorCategory, String> CATEGORY_DEMO_IMAGES = Map.ofEntries(
  Map.entry(VendorCategory.PHOTOGRAPHY, "/images/photography.jpg"),
  Map.entry(VendorCategory.VIDEOGRAPHY, "/images/photography.jpg"),
  Map.entry(VendorCategory.CATERING, "/images/catering.jpg"),
  Map.entry(VendorCategory.DECORATION, "/images/decoration.jpg"),
  Map.entry(VendorCategory.MUSIC_DJ, "/images/music-dj.jpg"),
  Map.entry(VendorCategory.WEDDING_DRESSES, "/images/wedding-dress.jpg"),
  Map.entry(VendorCategory.WEDDING_CAKES, "/images/decoration.jpg"),
  Map.entry(VendorCategory.BEAUTY_SALON, "/images/wedding-dress.jpg"),
  Map.entry(VendorCategory.WEDDING_PLANNERS, "/images/other-services.jpg"),
  Map.entry(VendorCategory.VENUES, "/images/venue.jpg"),
  Map.entry(VendorCategory.MAKEUP_ARTISTS, "/images/wedding-dress.jpg"),
  Map.entry(VendorCategory.BRIDAL_SALONS, "/images/wedding-dress.jpg"),
  Map.entry(VendorCategory.HAIR_STYLISTS, "/images/wedding-dress.jpg"),
  Map.entry(VendorCategory.PHOTO_BOOTH, "/images/photography.jpg"),
  Map.entry(VendorCategory.CONTENT_CREATOR, "/images/photography.jpg"),
  Map.entry(VendorCategory.WEDDING_OFFICIANTS, "/images/other-services.jpg"),
  Map.entry(VendorCategory.FLORISTS, "/images/florist.jpg"),
  Map.entry(VendorCategory.OTHER, "/images/other-services.jpg")
 );


 public VendorController(VendorService vendorService, ReviewService reviewService, BookingService bookingService) {
 this.vendorService = vendorService;
 this.reviewService = reviewService;
 this.bookingService = bookingService;
 }

 // ─── Vendor Catalog ──────────────────────────────────────────────────────────
 @GetMapping("/vendors")
 public String listVendors(@RequestParam(value = "query", required = false) String query,
 @RequestParam(value = "category", required = false) String categoryStr,
 @RequestParam(value = "filter", required = false) String filter,
 @RequestParam(value = "location", required = false) String location,
 HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

 VendorCategory category = null;
 if (categoryStr != null && !categoryStr.trim().isEmpty()) {
     try {
         category = VendorCategory.valueOf(categoryStr.trim().toUpperCase());
     } catch (IllegalArgumentException ex) {
         // An unknown category should return no results, not silently show all vendors.
         model.addAttribute("vendors", List.of());
         model.addAttribute("categories", VendorCategory.values());
         model.addAttribute("selectedCategory", categoryStr);
         model.addAttribute("query", query);
         model.addAttribute("filter", filter);
         model.addAttribute("location", location);
         model.addAttribute("pageTitle", "Vendors Directory - Éclat");
         model.addAttribute("activePage", "vendors");
         return "member03/vendors";
     }
 }

 final VendorCategory selectedCategory = category;
 List<Vendor> vendors;
 if (isAdmin && "PENDING".equalsIgnoreCase(filter)) {
     vendors = vendorService.getAllVendors().stream()
             .filter(v -> "PENDING".equalsIgnoreCase(v.getApprovalStatus()))
             .filter(v -> selectedCategory == null || v.getCategory() == selectedCategory)
             .filter(v -> query == null || query.trim().isEmpty()
                     || (v.getVendorName() != null && v.getVendorName().toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT))))
             .toList();
 } else if (isAdmin) {
     // Administrators can inspect all vendors, including pending/rejected records.
     vendors = vendorService.searchVendors(query, selectedCategory, null);
 } else {
     // Customers and guests can only browse approved, active vendors.
     vendors = vendorService.searchApprovedVendors(query, selectedCategory, null);
 }
 if (location != null && !location.trim().isEmpty()) {
  String district = location.trim().toLowerCase(Locale.ROOT);
  vendors = vendors.stream()
          .filter(v -> v.getAddress() != null && v.getAddress().toLowerCase(Locale.ROOT).contains(district))
          .toList();
 }

 model.addAttribute("vendors", vendors);
 model.addAttribute("vendorImageSources", vendors.stream().collect(java.util.stream.Collectors.toMap(Vendor::getVendorId, this::getVendorImageSource)));
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("selectedCategory", categoryStr);
 model.addAttribute("query", query);
 model.addAttribute("filter", filter);
 model.addAttribute("location", location);
 model.addAttribute("pageTitle", "Vendors Directory - Éclat");
 model.addAttribute("activePage", "vendors");
 return "member03/vendors";
 }

 // ─── Register New Vendor ─────────────────────────────────────────────────────
 @GetMapping("/vendors/new")
 public String showAddVendorForm(HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 model.addAttribute("vendorPhotoLimitBytes", MAX_VENDOR_IMAGE_BYTES);
 model.addAttribute("isEdit", false);
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("pageTitle", "Register Vendor - Éclat");
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-form";
 }

 @PostMapping("/vendors/new")
 public String registerVendor(@RequestParam("vendorName") String vendorName,
 @RequestParam("category") String categoryStr,
 @RequestParam("contactPerson") String contactPerson,
 @RequestParam("email") String email,
 @RequestParam("phone") String phone,
 @RequestParam("address") String address,
 @RequestParam("serviceDescription") String serviceDescription,
 @RequestParam(value = "basePrice", required = false) String basePriceValue,
 @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (actor == null) return "redirect:/login";
 try {
 VendorCategory category = VendorCategory.fromStringStrict(categoryStr);
 BigDecimal basePrice = parseBasePrice(basePriceValue);
 validateVendorImage(vendorImage);
 Vendor vendor = vendorService.addVendor(
 vendorName, category, contactPerson, email, phone, address, serviceDescription, basePrice);
 saveVendorImage(vendor.getVendorId(), vendorImage);
 redirectAttributes.addFlashAttribute("successMessage",
 "Vendor '" + vendor.getVendorName() + "' registered successfully! (Awaiting admin approval)");
 return "redirect:/vendors";
 } catch (AppException | IllegalArgumentException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/vendors/new";
 }
 }

 // ─── View Vendor Details ─────────────────────────────────────────────────────
 @GetMapping("/vendors/view/{id}")
 public String viewVendorDetail(@PathVariable("id") String id, HttpSession httpSession,
                                 Model model, RedirectAttributes redirectAttributes) {
 Vendor vendor = vendorService.getById(id);
 User viewer = (User) httpSession.getAttribute("currentUser");
 boolean admin = viewer != null && "ADMIN".equalsIgnoreCase(viewer.getRole());
 if (!admin && (!"APPROVED".equalsIgnoreCase(vendor.getApprovalStatus()) || !vendor.isActive())) {
  redirectAttributes.addFlashAttribute("errorMessage", "This vendor profile is not publicly available.");
  return "redirect:/vendors";
 }
 double avgRating = reviewService.getAverageRating(id);
 int reviewCount = reviewService.getByVendor(id).size();

 model.addAttribute("vendor", vendor);
 model.addAttribute("vendorImageSource", getVendorImageSource(vendor));
 model.addAttribute("avgRating", avgRating);
 model.addAttribute("reviewCount", reviewCount);
 model.addAttribute("pageTitle", vendor.getVendorName() + " - Vendor Details");
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-detail";
 }

 // ─── Edit Vendor ─────────────────────────────────────────────────────────────
 @GetMapping("/vendors/edit/{id}")
 public String showEditVendorForm(@PathVariable("id") String id, HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 if (!"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/vendors";

 Vendor vendor = vendorService.getById(id);
 model.addAttribute("vendor", vendor);
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("isEdit", true);
 model.addAttribute("pageTitle", "Edit Vendor - " + vendor.getVendorName());
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-form";
 }

 @PostMapping("/vendors/edit/{id}")
 public String updateVendor(@PathVariable("id") String id,
 @RequestParam("vendorName") String vendorName,
 @RequestParam("category") String categoryStr,
 @RequestParam("contactPerson") String contactPerson,
 @RequestParam("email") String email,
 @RequestParam("phone") String phone,
 @RequestParam("address") String address,
 @RequestParam("serviceDescription") String serviceDescription,
 @RequestParam(value = "basePrice", required = false) String basePriceValue,
 @RequestParam(value = "vendorImage", required = false) MultipartFile vendorImage,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (actor == null || !"ADMIN".equalsIgnoreCase(actor.getRole())) return "redirect:/login";
 try {
 VendorCategory category = VendorCategory.fromStringStrict(categoryStr);
 BigDecimal basePrice = parseBasePrice(basePriceValue);
 validateVendorImage(vendorImage);
 vendorService.updateVendor(id, vendorName, category, contactPerson, email, phone, address, serviceDescription, basePrice);
 saveVendorImage(id, vendorImage);
 redirectAttributes.addFlashAttribute("successMessage", "Vendor details updated successfully!");
 } catch (AppException | IllegalArgumentException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors/view/" + id;
 }

 // ─── Admin Moderation Actions ────────────────────────────────────────────────
 @PostMapping("/admin/vendors/approve/{id}")
 public String approveVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.approveVendor(id);
 redirectAttributes.addFlashAttribute("successMessage", "Vendor has been approved!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/reject/{id}")
 public String rejectVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.rejectVendor(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Vendor registration rejected.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/deactivate/{id}")
 public String deactivateVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.deactivateVendor(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Vendor deactivated.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }


 @PostMapping("/admin/vendors/activate/{id}")
 public String activateVendor(@PathVariable("id") String id, HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {
  User user = (User) httpSession.getAttribute("currentUser");
  if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";
  try {
   vendorService.activateVendor(id);
   redirectAttributes.addFlashAttribute("successMessage", "Vendor activated.");
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  }
  return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/delete/{id}")
 public String deleteVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 if (!bookingService.getByVendor(id).isEmpty()) {
  redirectAttributes.addFlashAttribute("errorMessage", "This vendor has booking history. Deactivate the vendor instead of deleting it.");
  return "redirect:/vendors";
 private Path getVendorImageDirectory() {
  return Path.of(FileUtil.getFilePath("vendor-images")).toAbsolutePath().normalize();
 }

 private Path findUploadedVendorImage(String id) {
  if (id == null || !id.matches("[A-Za-z0-9_-]{1,100}")) return null;
  Path directory = getVendorImageDirectory();
  for (String extension : ALLOWED_IMAGE_EXTENSIONS) {
   Path candidate = directory.resolve(id + "." + extension).normalize();
   if (candidate.startsWith(directory) && Files.isRegularFile(candidate)) return candidate;
  }
  return null;
 }

 private String getVendorImageSource(Vendor vendor) {
  Path upload = findUploadedVendorImage(vendor.getVendorId());
  if (upload != null) return "/vendor-images/" + vendor.getVendorId();
  return CATEGORY_DEMO_IMAGES.getOrDefault(vendor.getCategory(), "/images/other-services.jpg");
 }

 @GetMapping("/vendor-images/{id}")
 @ResponseBody
 public ResponseEntity<byte[]> getVendorImage(@PathVariable("id") String id) throws IOException {
  Path image = findUploadedVendorImage(id);
  if (image == null) return ResponseEntity.notFound().build();
  String filename = image.getFileName().toString().toLowerCase(Locale.ROOT);
  MediaType type = filename.endsWith(".png") ? MediaType.IMAGE_PNG
          : filename.endsWith(".gif") ? MediaType.IMAGE_GIF : MediaType.IMAGE_JPEG;
  return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
          .contentType(type).body(Files.readAllBytes(image));
 }

 @GetMapping("/api/vendors/{id}/photos")
 @ResponseBody
 public ResponseEntity<List<String>> getVendorPhotos(@PathVariable("id") String id) {
  Vendor vendor = vendorService.getById(id);
  List<String> photos = findUploadedVendorImage(id) == null
          ? List.of(getVendorImageSource(vendor)) : List.of(getVendorImageSource(vendor));
  return ResponseEntity.ok(photos);
 }

 @GetMapping("/vendor-images/category/{category}")
 @ResponseBody
 public ResponseEntity<byte[]> getCategoryDemoImage(@PathVariable("category") String category) throws IOException {
  VendorCategory selected;
  try { selected = VendorCategory.fromStringStrict(category); }
  catch (IllegalArgumentException ex) { return ResponseEntity.notFound().build(); }
  String resource = CATEGORY_DEMO_IMAGES.getOrDefault(selected, "/images/other-services.jpg");
  Path image = Path.of("src/main/resources/static" + resource).toAbsolutePath().normalize();
  if (!Files.isRegularFile(image)) return ResponseEntity.notFound().build();
  MediaType type = resource.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
  return ResponseEntity.ok().contentType(type).body(Files.readAllBytes(image));
 }

 private String validateVendorImage(MultipartFile image) {
  if (image == null || image.isEmpty()) return null;
  if (image.getSize() > MAX_VENDOR_IMAGE_BYTES) throw new IllegalArgumentException("Vendor image must be 5 MB or smaller.");
  String original = image.getOriginalFilename() == null ? "" : image.getOriginalFilename();
  int dot = original.lastIndexOf('.');
  String extension = dot < 0 ? "" : original.substring(dot + 1).toLowerCase(Locale.ROOT);
  if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) throw new IllegalArgumentException("Only JPG, PNG or GIF images are allowed.");
  try {
   if (ImageIO.read(new ByteArrayInputStream(image.getBytes())) == null) throw new IllegalArgumentException("The selected file is not a valid image.");
  } catch (IOException ex) { throw new IllegalArgumentException("Unable to read the selected image.", ex); }
  return extension;
 }

 private void saveVendorImage(String vendorId, MultipartFile image) {
  if (image == null || image.isEmpty()) return;
  String extension = validateVendorImage(image);
  try {
   byte[] bytes = image.getBytes();
   Path directory = getVendorImageDirectory();
   Files.createDirectories(directory);
   Path target = directory.resolve(vendorId + "." + extension).normalize();
   if (!target.startsWith(directory)) throw new IllegalArgumentException("Invalid image name.");
   Files.write(target, bytes);
   for (String oldExtension : ALLOWED_IMAGE_EXTENSIONS) {
    if (!oldExtension.equals(extension)) Files.deleteIfExists(directory.resolve(vendorId + "." + oldExtension));
   }
  } catch (IOException ex) { throw new IllegalStateException("Unable to save vendor image. Please try again.", ex); }
 }
 
bstring(dot + 1).toLowerCase(Locale.ROOT);
  if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
   throw new IllegalArgumentException("Only JPG, PNG or GIF images are allowed.");
  }
  try {
   if (ImageIO.read(new ByteArrayInputStream(image.getBytes())) == null) {
    throw new IllegalArgumentException("The selected file is not a valid image.");
   }
  } catch (IOException ex) {
   throw new IllegalArgumentException("Unable to read the selected image.", ex);
  }
  return extension;
 }

 private void saveVendorImage(String vendorId, MultipartFile image) {
  if (image == null || image.isEmpty()) return;
  String extension = validateVendorImage(image);
  try {
   byte[] bytes = image.getBytes();
   Path directory = Path.of(FileUtil.getFilePath("vendor-images")).toAbsolutePath().normalize();
   Files.createDirectories(directory);
   Path target = directory.resolve(vendorId + "." + extension).normalize();
   if (!target.startsWith(directory)) throw new IllegalArgumentException("Invalid image name.");
   Files.write(target, bytes);
   for (String oldExtension : ALLOWED_IMAGE_EXTENSIONS) {
    if (!oldExtension.equals(extension)) Files.deleteIfExists(directory.resolve(vendorId + "." + oldExtension));
   }
  } catch (IOException ex) {
   throw new IllegalStateException("Unable to save vendor image. Please try again.", ex);
  }
 }

 private BigDecimal parseBasePrice(String value) {
  if (value == null || value.trim().isEmpty()) {
   throw new com.wedding.common.exceptions.ValidationException("Base price is required.");
  }
  try {
   return new BigDecimal(value.trim());
  } catch (NumberFormatException ex) {
   throw new com.wedding.common.exceptions.ValidationException("Base price must be a valid number.");
  }
 }
}
