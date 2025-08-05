# 人力資源管理系統 (Human Resource Management System)

這是一個以 **Spring Boot** 和 **Angular** 開發的全端人力資源管理系統專案。我主要負責後端的 **員工管理** 與 **薪資系統** 模組，旨在提供一套現代化且高效的解決方案，簡化人資部門的日常作業流程。

## 主要功能與個人貢獻 (Main Functions & My Contribution)

我在專案中主要負責開發與維護兩個後端模組：**員工管理系統** 和 **薪資系統**。

---

### 員工管理系統 (Employee Management Module)

#### 我的貢獻：

- 從零開始設計與實作員工基本資料的 **新增、查詢、修改、刪除（CRUD）** 功能。
- 開發使用者的 **登入、登出、密碼更新** 等功能，實現基本身分驗證流程。
- 建立員工 **在職、離職、復職** 狀態管理機制，確保資料的正確性。

#### 我的學習：

- 學會將複雜的業務邏輯轉化為清晰、可維護的後端程式碼。
- 掌握 API 的設計與開發流程，從接收前端請求到資料庫處理與回應回傳。
- 理解資料庫的關聯設計，並確保資料的一致性與完整性。

---

### 薪資系統 (Payroll Module)

#### 我的貢獻：

- 實作薪資單的 **新增、查詢、修改、刪除** 等核心功能。
- 開發根據公司內部規則進行的 **薪資計算輔助功能**，輔助會計部門初步核算薪資。
- 設計多種查詢方式，支援以 **員工 ID、年份、月份** 為條件快速篩選薪資單資料。

#### 我的學習：

- 深入理解後端進行金額與數字運算時如何確保計算的正確性與準確性。
- 學會將企業內部的業務規則轉化為後端邏輯並實作。

## 技術棧 (Technology Stack)

### 後端 (Backend)
-   **框架**: Spring Boot
-   **持久層**: Mybatis-Plus
-   **建置工具**: Gradle
-   **開發工具**: Eclipse

### 前端 (Frontend)
-   **框架**: Angular
-   **開發工具**: Visual Studio Code

### 資料庫與版本控制
-   **資料庫**: MySQL
-   **資料庫管理工具**: MySQL Workbench
-   **版本控制**: Git

---

## GitHub 儲存庫結構 (Repository Structure)
此儲存庫包含專案的三個主要部分：
-   `HRMS_BACK`: 後端 Spring Boot 專案。
-   `HRMS_FRONT`: 前端 Angular 專案。
-   `MySQL`: 包含資料庫 Schema 的 SQL 檔案。

---

## 如何運行專案 (How to Run This Project)

### 前提條件 (Prerequisites)
-   Java Development Kit (JDK) 8 或更高版本
-   Node.js & npm
-   MySQL 資料庫
-   Angular CLI (使用 `npm install -g @angular/cli` 安裝)

### 步驟 (Steps)
1.  **資料庫設定**
    -   在 MySQL 中建立一個名為 `hr_management_system` 的資料庫。
    -   匯入 `MySQL/schema.sql` 檔案以建立資料表結構。
    -   (可選) 匯入 `MySQL/data.sql` 檔案以匯入測試資料。
2.  **後端設定與啟動**
    -   進入 `HRMS_BACK` 資料夾。
    -   編輯 `src/main/resources/application.properties` 檔案，填寫資料庫連線等資訊。
    -   進行 `Gradle Refresh`。
    -   使用 IDE（如 Eclipse）啟動後端應用程式。
3.  **前端設定與啟動**
    -   進入 `HRMS_FRONT` 資料夾。
    -   安裝所有 Node.js 依賴套件，執行 `npm install`。
    -   啟動 Angular 開發伺服器，執行 `ng serve`。
    -   打開瀏覽器並訪問 `http://localhost:4200`。

### 測試帳號清單 (Test account list)
以下是為系統建立的假資料，用於展示不同部門和職位的權限與功能。所有帳號的預設密碼都與帳號（電子信箱）相同。

| 部門 | 職位 | 帳號（電子信箱） | 預設密碼 |
| :--- | :--- | :--- | :--- |
| **老闆** | BOSS | `maru@gmail.com` | `maru@gmail.com` |
| **人資部** | HR_MANAGER | `melody@gmail.com` | `melody@gmail.com` |
| **人資部** | HR_EMPLOYEE | `Anna@gmail.com` | `Anna@gmail.com` |
| **會計部** | ACCOUNTANT_MANAGER | `Alan@gmail.com` | `Alan@gmail.com` |
| **會計部** | ACCOUNTANT_EMPLOYEE | `David@gmail.com` | `David@gmail.com` |
| **總務部** | GENERAL_AFFAIRS_MANAGER | `Shu@gmail.com` | `Shu@gmail.com` |
| **總務部** | GENERAL_AFFAIRS_EMPLOYEE | `Vera@gmail.com` | `Vera@gmail.com` |

### 結語 (Conclusion)

本專案為我在後端開發能力上的實作練習與成果展示，透過實際模組開發與系統整合，我深入理解了 API 設計、業務邏輯建構與資料處理流程。
若您對專案內容有進一步的興趣或想了解細節，歡迎在面試時與我討論，我非常樂意分享我的開發思路與學習過程。

---
---

# 人事管理システム (Human Resource Management System)

これは **Spring Boot** と **Angular** を使用して開発されたフルスタックの人事管理システムです。私は主にバックエンドの **従業員管理** と **給与システム** モジュールの開発を担当しました。目的は、人事部門の日常業務を効率化する、現代的で高性能なソリューションを提供することです。

## 主な機能と個人の貢献 (Main Functions & My Contribution)

このプロジェクトでは、私は主に以下の2つのバックエンドモジュールの開発と保守を担当しました：**従業員管理システム** および **給与システム**。

---

### 従業員管理システム (Employee Management Module)

#### 担当内容：

- 従業員の基本情報に関する **新規作成、検索、更新、削除（CRUD）** 機能をゼロから設計・実装。
- ユーザーの **ログイン、ログアウト、パスワード更新** など、基本的な認証機能を開発。
- 従業員の **在職、退職、復職** 状態を管理する仕組みを構築し、データの整合性を確保。

#### 学んだこと：

- 複雑な業務ロジックを分かりやすく、保守性の高いバックエンドコードに変換する方法を習得。
- フロントエンドからのリクエスト受付から、データベース処理、レスポンス返却までの一連の **API設計と開発プロセス** を理解。
- データベースのリレーション設計を学び、データの一貫性と整合性を保つ方法を身につけた。

---

### 給与システム (Payroll Module)

#### 担当内容：

- 給与明細の **新規作成、検索、更新、削除** といった主要機能を実装。
- 社内の給与ルールに基づいて自動で計算する **給与計算補助機能** を開発し、経理部門の初期確認を支援。
- **従業員ID、年、月** などを条件とした多様な検索機能を設計し、給与データの抽出を効率化。

#### 学んだこと：

- 金額や数値計算の正確性・精度を保ちながら、バックエンドで処理する方法を学習。
- 企業の業務ルールをロジックとしてバックエンドに実装する方法を理解。
- フロントエンドチームとの連携の重要性を実感し、APIが求められるデータ構造に適合しているかを確認することの大切さを学んだ。

## 技術スタック (Technology Stack)

### バックエンド (Backend)
-   フレームワーク: Spring Boot
-   永続化層: Mybatis-Plus
-   ビルドツール: Gradle
-   開発環境: Eclipse

### フロントエンド (Frontend)
-   フレームワーク: Angular
-   開発環境: Visual Studio Code

### データベースとバージョン管理
-   データベース: MySQL
-   データベース管理ツール: MySQL Workbench
-   バージョン管理: Git

---

## GitHub リポジトリ構成 (Repository Structure)

本リポジトリは以下の3つの主要部分で構成されています：

-   `HRMS_BACK`: バックエンド（Spring Boot プロジェクト）
-   `HRMS_FRONT`: フロントエンド（Angular プロジェクト）
-   `MySQL`: データベーススキーマとサンプルデータの SQL ファイル

---

## プロジェクトの実行方法 (How to Run This Project)

### 前提条件 (Prerequisites)
-   Java Development Kit (JDK) 8 以上
-   Node.js および npm
-   MySQL データベース
-   Angular CLI（`npm install -g @angular/cli` でインストール）

### 実行手順 (Steps)

1.  **データベースの設定**
    -   MySQL にて `hr_management_system` という名前のデータベースを作成。
    -   `MySQL/schema.sql` ファイルをインポートしてテーブル構造を作成。
    -   （任意）`MySQL/data.sql` をインポートしてテストデータを追加。

2.  **バックエンドの設定と起動**
    -   `HRMS_BACK` ディレクトリに移動。
    -   `src/main/resources/application.properties` を編集し、データベース接続情報を設定。
    -   `Gradle Refresh` を実行。
    -   Eclipse などの IDE でアプリケーションを起動。

3.  **フロントエンドの設定と起動**
    -   `HRMS_FRONT` ディレクトリに移動。
    -   `npm install` で必要な依存パッケージをインストール。
    -   `ng serve` を実行して Angular 開発サーバーを起動。
    -   ブラウザで `http://localhost:4200` にアクセス。

### テストアカウントリスト (Test account list)
システムのために作成した、部署や役職ごとの権限と機能を示すためのダミーデータです。すべてのアカウントの初期パスワードは、アカウント（メールアドレス）と同一です。

| 部門 | 役職 | アカウント（メールアドレス） | 初期パスワード |
| :--- | :--- | :--- | :--- |
| **社長** | BOSS | `maru@gmail.com` | `maru@gmail.com` |
| **人事部** | HR_MANAGER | `melody@gmail.com` | `melody@gmail.com` |
| **人事部** | HR_EMPLOYEE | `Anna@gmail.com` | `Anna@gmail.com` |
| **経理部** | ACCOUNTANT_MANAGER | `Alan@gmail.com` | `Alan@gmail.com` |
| **経理部** | ACCOUNTANT_EMPLOYEE | `David@gmail.com` | `David@gmail.com` |
| **総務部** | GENERAL_AFFAIRS_MANAGER | `Shu@gmail.com` | `Shu@gmail.com` |
| **総務部** | GENERAL_AFFAIRS_EMPLOYEE | `Vera@gmail.com` | `Vera@gmail.com` |

### 最後に (Conclusion)

本プロジェクトは、バックエンド開発における私の実践力と学習成果を示すものです。業務ロジックの設計や API の構築を通じて、システム開発全体の流れを深く理解することができました。
面接の際に本プロジェクトについてご質問があれば、ぜひお気軽にお聞きください。私の開発プロセスや考え方について、詳しくご説明いたします。
