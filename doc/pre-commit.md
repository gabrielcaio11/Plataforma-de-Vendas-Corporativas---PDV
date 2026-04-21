
# Execute antes de commitar

```
mvn spotless:apply
```

```
mvn checkstyle:check
```

# Explicação linha por linha

```
mvn spotless:apply
```

* Formata todo o código
* Remove imports não usados
* Aplica padrão (ex: Google)

``` 
mvn checkstyle:check
```

* Executa o Checkstyle
* Valida regras (complexidade, naming, etc.)
