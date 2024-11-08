# Репозиторий с материалами по курсу Java

Требуется Java версии 17.
Gradle Wrapper версии 7.3. будет подключен в ветке с заданиями.

## Порядок сдачи заданий

0. В своём форке создать две метки для реквестов (_Issues_ -> _Labels_)
- `Ready to check` (синяя)
- `Wait for fixes` (оранжевая)
- Также для каждого нового задания нужно будет добавлять по метке вида `task-N` или `control-N` серого цвета (`#36454f`)

1. Сделать форк данного репозитория и добавить основной репозиторий к своему как `upstream`:

```
git remote add upstream git@gitlab.com:sloboegen98/java-1-2024.git
```

Дать `sloboegen98`, `DKARAGODIN` и своему проверяющему (`asurkis`, `OldKrabik` и `ritka-admin`) доступ к этому форку (с уровнем прав не ниже Developer).

2. Домашки выкладываются в ветки вида `task-N-title`, контрольные в ветки `control-N`

3. Для сдачи задания требуется добавить ветку из основного репозитория в свой

```
git fetch upstream
git checkout upstream/task-N-title
git checkout -b task-N-title
git push origin task-N-title
```

4. Создать ветку `task-N-title-dev`, в которую добавляется решение

```
git checkout -b task-N-title-dev
```

5. При готовности задания запушить `dev` ветку и создать Merge Request.
   Заголовок MR должен быть в следующем виде: **Фамилия Имя. Task N** или **Фамилия Имя. Control N**.
   В данном реквесте добавить проверяющего в качестве _Assignee_, добавить метку `Ready to check` и метку `task-N` или `control-N`.  
   **NB**: MR необходимо открывать внутри веток своего репозитория

6. После проверки MR либо закрывается (если всё хорошо) или на него ставится метка `Wait for fixes`, если требуются исправления

7. Исправления добавляются в ту же ветку в тот же самый MR (новый MR создавать не нужно). После отправки всех исправлений нужно снова выставить метку `Ready to check`. **Никакие другие метки трогать не нужно**

- После сдачи к делайну задание проверяется, после чего у вас есть неделя (начиная от момента проверки) на то, чтобы прислать исправления (если требуется). После этой недели исправления не принимаются
- MR не в ту ветку не проверяются
- MR без выставленной метки `Ready to check` не проверяются
