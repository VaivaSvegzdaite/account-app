package com.example.sas.account;

import com.example.sas.account.dto.TransactionRequest;
import com.example.sas.account.dto.TransferRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/api/account")
@Tag(name = "AccountController", description = "Controller defines entry points for accounts operations")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //requests for rendering view via templates and update db

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "text/html")
    public String showAccountList(Model model, @RequestParam(required = false) String name,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "9") int size) {
        try {
            List<Account> accounts = new ArrayList<>();
            Pageable paging = PageRequest.of(page - 1, size);

            Page<Account> pageAcc;
            if (name == null) {
                pageAcc = accountService.findAll(paging);
            } else {
                pageAcc = accountService.searchByName(name, paging);
                model.addAttribute("name", name);
            }

            accounts = pageAcc.getContent();

            model.addAttribute("accounts", accounts);
            model.addAttribute("currentPage", pageAcc.getNumber() + 1);
            model.addAttribute("totalItems", pageAcc.getTotalElements());
            model.addAttribute("totalPages", pageAcc.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "account-list";
    }

    @RequestMapping(value = "/addaccount", method = RequestMethod.GET, produces = "text/html")
    public String showAccountAddForm(Account account) {
        return "add-account";
    }

    @RequestMapping(value = "/processaddaccount", method = RequestMethod.POST, produces = "text/html")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-account";
        }
        accountService.saveAccount(account);
        return "redirect:/api/account/list";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET, produces = "text/html")
    public String showUpdateAccountForm(@PathVariable("id") Long id, Model model) {
        var oAccount = accountService.getAccountById(id);
        oAccount.ifPresent(account -> model.addAttribute("account", account));
        return "update-account";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, produces = "text/html")
    public String updateAccount(@PathVariable("id") Long id, @Valid Account account,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            account.setId(id);
            return "update-account";
        }

        accountService.updateAccount(id, account);
        return "redirect:/api/account/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model) {
        var oAccount = accountService.getAccountById(id);
        if (oAccount.isPresent()) {
            accountService.deleteAccount(id);
        }
        return "redirect:/api/account/list";
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET, produces = "text/html")
    public String showAccountDetails(@PathVariable("id") Long id, Model model) {
        var oAccount = accountService.getAccountById(id);
        oAccount.ifPresent(account -> model.addAttribute("account", account));
        return "account-details";
    }

    @RequestMapping(value = "/deposit/{id}", method = RequestMethod.POST, produces = "text/html")
    public String depositFunds(@PathVariable("id") Long id, @RequestParam("depositAmount") BigDecimal amount) {
        accountService.depositToAccount(id, amount);
        return "redirect:/api/account/details/" + id;
    }

    @RequestMapping(value = "/withdraw/{id}", method = RequestMethod.POST, produces = "text/html")
    public String withdrawFunds(@PathVariable("id") Long id, @RequestParam("withdrawAmount") BigDecimal amount) {
        accountService.withdrawFromAccount(id, amount);
        return "redirect:/api/account/details/" + id;
    }

    @RequestMapping(value = "/transfer/{id}", method = RequestMethod.POST, produces = "text/html")
    public String transferFunds(@PathVariable("id") Long id, @RequestParam("recipientAccount") Long destinationId, @RequestParam("transferAmount") BigDecimal amount) {
        accountService.transferBetweenAccounts(id, destinationId, amount);
        return "redirect:/api/account/details/" + id;
    }

    //rest requests send postman and update db

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> optionalAccount = accountService.getAccountById(id);
        return optionalAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Long>> saveAccount(@RequestBody Account account) {
        accountService.saveAccount(account);

        Map<String, Long> response = new HashMap<>();
        response.put("id", account.getId());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        accountService.updateAccount(id, account);
        return ResponseEntity.ok("Account was updated!");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account was deleted!");
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<String> depositToAccount(
            @PathVariable Long id,
            @RequestBody TransactionRequest transactionRequest
    ) {
        try {
            accountService.depositToAccount(id, transactionRequest.getAmount());
            return ResponseEntity.ok("Deposit successful!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<String> withdrawFromAccount(
            @PathVariable Long id,
            @RequestBody TransactionRequest transactionRequest
    ) {
        try {
            accountService.withdrawFromAccount(id, transactionRequest.getAmount());
            return ResponseEntity.ok("Withdrawal successful!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }


    @PostMapping("/{fromId}/transfer")
    public ResponseEntity<String> transferBetweenAccounts(
            @PathVariable Long fromId,
            @RequestBody TransferRequest transferRequest
    ) {
        try {
            accountService.transferBetweenAccounts(fromId, transferRequest.getDestinationId(), transferRequest.getAmount());
            return ResponseEntity.ok("Transfer request sent!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }
}
