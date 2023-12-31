package ec.edu.espe.arquitectura.banquito.loan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ec.edu.espe.arquitectura.banquito.loan.dto.GuarantyRQ;
import ec.edu.espe.arquitectura.banquito.loan.dto.LoanProductRS;
import ec.edu.espe.arquitectura.banquito.loan.dto.LoanRQ;
import ec.edu.espe.arquitectura.banquito.loan.dto.LoanRS;
import ec.edu.espe.arquitectura.banquito.loan.model.Guaranty;
import ec.edu.espe.arquitectura.banquito.loan.model.Loan;
import ec.edu.espe.arquitectura.banquito.loan.service.LoanService;

@RestController
@RequestMapping("/api/v2/loans")
public class LoanController {
    private final LoanService loanService;
    private final RestTemplate restTemplate;

    public LoanController(LoanService loanService, RestTemplate restTemplate) {
        this.loanService = loanService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<LoanRS> obtainByUuid(
            @PathVariable(name = "uuid") String uuid) {
        try {
            LoanRS loan = this.loanService.listById(uuid);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRQ loan) {
        try {
            Loan loanRS = this.loanService.createLoan(loan);
            return ResponseEntity.ok(loanRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

    @PostMapping("/guaranty")
    public ResponseEntity<Guaranty> createGuaranty(@RequestBody GuarantyRQ guaranty) {
        try {
            Guaranty guarantyRS = this.loanService.createGuaranty(guaranty);
            return ResponseEntity.ok(guarantyRS);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();

        }
    }

    @PutMapping("/addGuaranty/{uuid}")
    public ResponseEntity<String> addGuarantyToLoan(@RequestBody LoanRQ loan,
            @PathVariable(name = "uuid") String uuid) {
        try {
            // Loan loanRS = this.loanService.addGuarantyToLoan(loan);
            this.loanService.addGuarantyToLoan(loan, uuid);
            return ResponseEntity.ok("ok");
        } catch (RuntimeException rte) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rte.getMessage());

        }
    }

    @GetMapping("/products/{uuid}")
    public ResponseEntity<LoanProductRS> obtainProductByUuid(
            @PathVariable(name = "uuid") String uuid) {
        try {
            String url = "http://localhost:9004/api/v1/loanProduct/productos/" + uuid;
            LoanProductRS loanProduct = this.restTemplate.getForObject(url, LoanProductRS.class);
            return ResponseEntity.ok(loanProduct);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/products")
    public ResponseEntity<LoanProductRS[]> obtainProducts() {
        try {
            String url = "http://localhost:9004/api/v1/loanProduct/productos";
            LoanProductRS[] products = this.restTemplate.getForEntity(url, LoanProductRS[].class).getBody();
            return ResponseEntity.ok(products);
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

}
