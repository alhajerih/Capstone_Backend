//package com.example.Shares.QRcode;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/user")
//public class QRCodeController {
//
//    @Autowired
//    private QRCodeService qrCodeService;
//
//    // Endpoint to save QR code
//    @PostMapping("/qrcode")
//    public ResponseEntity<QRCodeEntity> saveQRCode(@RequestBody QRCodeEntity qrCodeEntity) {
//        QRCodeEntity savedQRCode = qrCodeService.saveQRCode(qrCodeEntity);
//        return ResponseEntity.ok(savedQRCode);
//    }
//
//    // Endpoint to get QR code by transaction ID
//    @GetMapping("/{transactionId}")
//    public ResponseEntity<QRCodeEntity> getQRCode(@PathVariable Long transactionId) {
//        Optional<QRCodeEntity> qrCode = qrCodeService.findByTransactionId(transactionId);
//        return qrCode.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//}