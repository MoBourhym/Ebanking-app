# Rapport de Réalisation - Projet JEE Spring Angular JWT - Digital Banking

## Table des Matières

1. [Présentation du Projet](#1-présentation-du-projet)
2. [Architecture et Technologies](#2-architecture-et-technologies)
3. [Structure du Projet](#3-structure-du-projet)
4. [Création des Entités JPA](#4-création-des-entités-jpa)
5. [Couche Repository](#5-couche-repository)
6. [Couche Service et DTOs](#6-couche-service-et-dtos)
7. [Mappers](#7-mappers)
8. [Sécurité JWT](#8-sécurité-jwt)
9. [Controllers REST](#9-controllers-rest)
10. [Tests et Documentation](#10-tests-et-documentation)
11. [Configuration](#11-configuration)
12. [Fonctionnalités Implémentées](#12-fonctionnalités-implémentées)

---

## 1. Présentation du Projet

### Objectif

Développer une application de gestion bancaire digitale permettant de :

- Gérer des clients
- Créer et administrer des comptes bancaires (courants et épargne)
- Effectuer des opérations bancaires (débit, crédit, virement)
- Sécuriser l'accès avec JWT
- Exposer des API REST documentées

### Fonctionnalités Principales

- **Gestion des clients** : CRUD complet avec sécurité basée sur les rôles
- **Comptes bancaires** : Support des comptes courants (avec découvert) et comptes épargne (avec taux d'intérêt)
- **Opérations bancaires** : Débit, crédit, virements avec historique paginé
- **Authentification JWT** : Système complet d'authentification et autorisation
- **API REST** : Documentation automatique avec Swagger/OpenAPI

---

## 2. Architecture et Technologies

### Stack Technique

- **Framework Principal** : Spring Boot 3.4.5
- **Base de données** : MySQL (avec support H2 pour tests)
- **ORM** : Hibernate/JPA
- **Sécurité** : Spring Security + JWT
- **Documentation API** : SpringDoc OpenAPI (Swagger)
- **Build** : Maven
- **Version Java** : 17

### Dépendances Clés

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.8</version>
</dependency>
```

---

## 3. Structure du Projet

```
src/main/java/com/spring/ebankingbackend/
├── EbankingBackendApplication.java
├── dtos/                           # Data Transfer Objects
│   ├── CustomerDTO.java
│   ├── BankAccountDTO.java
│   ├── CurrentBankAccountDTO.java
│   ├── SavingBankAccountDTO.java
│   ├── AccountOperationDTO.java
│   ├── AccountHistoryDTO.java
│   ├── CreditDTO.java
│   ├── DebitDTO.java
│   └── TransferRequestDTO.java
├── entities/                       # Entités JPA
│   ├── Customer.java
│   ├── BankAccount.java
│   ├── CurrentAccount.java
│   ├── SavingAccount.java
│   └── AccountOperation.java
├── enums/                         # Énumérations
│   ├── AccountStatus.java
│   └── OperationType.java
├── exceptions/                    # Exceptions métier
│   ├── CustomerNotFoundException.java
│   ├── BankAccountNotFoundException.java
│   └── BalanceNotSufficentException.java
├── mappers/                       # Mappers Entity <-> DTO
│   └── BankAccountMapperImpl.java
├── repositories/                  # Repositories Spring Data
│   ├── CustomerRepository.java
│   ├── BankAccountRepository.java
│   └── AccountOperationRepository.java
├── security/                      # Module Sécurité JWT
│   ├── config/
│   ├── dto/
│   ├── entities/
│   ├── jwt/
│   ├── repositories/
│   ├── service/
│   └── web/
├── services/                      # Services métier
│   ├── BankAccountService.java
│   ├── BankAccountServiceImpl.java
│   └── BankService.java
└── web/                          # Controllers REST
    ├── BankAccountRestController.java
    └── CustomerWebController.java
```

---

## 4. Création des Entités JPA

### 4.1 Entité Customer

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
  
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<BankAccount> bankAccounts;
}
```

### 4.2 Entité BankAccount (Classe Abstraite)

```java
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 4)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createdAt;
  
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
  
    @ManyToOne
    private Customer customer;
  
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
```

**Concepts JPA utilisés :**

- **Héritage SINGLE_TABLE** : Toutes les classes filles dans une seule table
- **DiscriminatorColumn** : Colonne TYPE pour distinguer les types de comptes
- **Relations bidirectionnelles** : @OneToMany/@ManyToOne

### 4.3 Entités Filles

```java
@Entity
@DiscriminatorValue("CA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccount extends BankAccount {
    private Double overDraft; // Découvert autorisé
}

@Entity
@DiscriminatorValue("SA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount extends BankAccount {
    private Double interestRate; // Taux d'intérêt
}
```

### 4.4 Entité AccountOperation

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private Double amount;
    private String description;
  
    @Enumerated(EnumType.STRING)
    private OperationType type; // DEBIT ou CREDIT
  
    @ManyToOne
    private BankAccount bankAccount;
}
```

---

## 5. Couche Repository

### Repositories Spring Data JPA

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Méthodes CRUD automatiques
}

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    // ID String pour les comptes
}

@Repository
public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByBankAccountId(String accountId);
    Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(
        String accountId, Pageable pageable);
}
```

**Avantages Spring Data :**

- Génération automatique des méthodes CRUD
- Requêtes dérivées du nom des méthodes
- Support de la pagination avec `Page<T>`

---

## 6. Couche Service et DTOs

### 6.1 Interface Service

```java
public interface BankAccountService {
    // Gestion des clients
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    List<CustomerDTO> listCustormers();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
  
    // Gestion des comptes
    CurrentBankAccountDTO saveCurrentBankAccount(Double initialBalance, 
        double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(Double initialBalance, 
        double interestRate, Long customerId) throws CustomerNotFoundException;
    BankAccountDTO getBankAccount(String accountId);
    List<BankAccountDTO> bankAccountList();
  
    // Opérations bancaires
    void debit(String accountId, Double amount, String description) 
        throws BalanceNotSufficentException, BankAccountNotFoundException;
    void credit(String accountId, Double amount, String description) 
        throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, Double amount) 
        throws BankAccountNotFoundException, BalanceNotSufficentException;
  
    // Historique
    List<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) 
        throws BankAccountNotFoundException;
}
```

### 6.2 DTOs (Data Transfer Objects)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}

@Data
public class BankAccountDTO {
    private String id;
    private Double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private String type; // Discriminant pour le type de compte
}
```

**Avantages des DTOs :**

- Séparation entre la couche de présentation et le modèle de données
- Contrôle des données exposées dans l'API
- Évite l'exposition directe des entités JPA

---

## 7. Mappers

### Mapper Implementation

```java
@Service
public class BankAccountMapperImpl {
  
    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }
  
    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }
  
    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount, dto);
        dto.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
        dto.setType(savingAccount.getClass().getSimpleName());
        return dto;
    }
  
    // ... autres méthodes de mapping
}
```

---

## 8. Sécurité JWT

### 8.1 Configuration Sécurité

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/test/public").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
        return http.build();
    }
}
```

### 8.2 Service JWT

```java
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
  
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // 24 heures
  
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration; // 7 jours
  
    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }
  
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
  
    private String buildToken(Map<String, Object> extraClaims, 
                             UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
```

### 8.3 Entités Sécurité

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
  
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> roles = new ArrayList<>();
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
```

---

## 9. Controllers REST

### 9.1 Authentication Controller

```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
  
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "username", request.getUsername()
        ));
    }
  
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
```

### 9.2 Customer Controller

```java
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerWebController {
  
    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustormers();
    }
  
    @PostMapping("/customers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        return bankAccountService.saveCustomer(customerDTO);
    }
  
    @DeleteMapping("/customers/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCustomer(@PathVariable Long customerId) {
        bankAccountService.deleteCustomer(customerId);
    }
}
```

### 9.3 Bank Account Controller

```java
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class BankAccountRestController {
  
    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) 
            throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }
  
    @GetMapping("/accounts/{accountId}/pageOperations")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public AccountHistoryDTO getHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) 
            throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }
  
    @PostMapping("/accounts/debit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) 
            throws BankAccountNotFoundException, BalanceNotSufficentException {
        bankAccountService.debit(debitDTO.getAccountId(), 
                                debitDTO.getAmount(), 
                                debitDTO.getDescription());
        return debitDTO;
    }
  
    @PostMapping("/accounts/transfer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) 
            throws BankAccountNotFoundException, BalanceNotSufficentException {
        bankAccountService.transfer(
            transferRequestDTO.getAccountSource(),
            transferRequestDTO.getAccountDestination(),
            transferRequestDTO.getAmount());
    }
}
```

---

## 10. Tests et Documentation

### 10.1 Documentation Swagger

L'application génère automatiquement la documentation API via SpringDoc OpenAPI.

**Accès Swagger UI :** `http://localhost:8085/swagger-ui.html`

[Image Swagger UI ici - Interface de documentation interactive]

### 10.2 Endpoints API Disponibles

#### Authentification (Public)

- `POST /api/v1/auth/register` - Inscription
- `POST /api/v1/auth/authenticate` - Connexion

#### Clients (Authentifié)

- `GET /customers` - Liste des clients (USER/ADMIN)
- `GET /customers/{id}` - Détails client (USER/ADMIN)
- `POST /customers` - Créer client (ADMIN)
- `PUT /customers/{id}` - Modifier client (ADMIN)
- `DELETE /customers/{id}` - Supprimer client (ADMIN)

#### Comptes (Authentifié)

- `GET /accounts` - Liste des comptes (USER/ADMIN)
- `GET /accounts/{id}` - Détails compte (USER/ADMIN)
- `GET /accounts/{id}/operations` - Historique complet (USER/ADMIN)
- `GET /accounts/{id}/pageOperations` - Historique paginé (USER/ADMIN)

#### Opérations (Admin seulement)

- `POST /accounts/debit` - Débit
- `POST /accounts/credit` - Crédit
- `POST /accounts/transfer` - Virement

#### Tests de sécurité

- `GET /api/test/public` - Accès libre
- `GET /api/test/user` - Accès USER
- `GET /api/test/admin` - Accès ADMIN

---

## 11. Configuration

### 11.1 Configuration Base de Données

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bank?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Server Configuration
server.port=8085

# JWT Configuration
application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000
```

### 11.2 CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(true);
  
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 12. Fonctionnalités Implémentées

### Fonctionnalités Complètes

1. **Gestion des Entités JPA**

   - Héritage SINGLE_TABLE pour BankAccount
   - Relations bidirectionnelles optimisées
   - Énumérations pour les statuts et types d'opérations
2. **Couche Repository**

   - Spring Data JPA avec requêtes dérivées
   - Pagination pour l'historique des opérations
   - Repositories typés pour chaque entité
3. **Couche Service**

   - Pattern DTO avec mappers
   - Gestion transactionnelle avec @Transactional
   - Exceptions métier personnalisées
4. **Sécurité JWT Complète**

   - Authentification stateless
   - Autorisation basée sur les rôles (@PreAuthorize)
   - Tokens JWT avec refresh tokens
   - Filtres de sécurité personnalisés
5. **API REST**

   - Controllers RESTful avec bonnes pratiques
   - Gestion des erreurs centralisée
   - Support CORS pour frontend Angular
6. **Documentation**

   - Swagger/OpenAPI automatique
   - Documentation interactive des endpoints
7. **Initialisation des Données**

   - CommandLineRunner pour créer des données de test
   - Utilisateur admin par défaut (admin/admin123)
   - Génération automatique de clients et comptes
     ---
