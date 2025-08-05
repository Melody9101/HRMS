# 人力資源管理系統 (Human Resource Management System)

這是一個基於 Spring Boot 框架所開發的人力資源管理系統（HRMS），用以解決傳統人事作業流程中的資料分散與流程繁瑣問題。本系統將員工的完整生命週期資訊，從入職資料、考勤數據、假別申請至薪資明細，集中在一個平台上管理。此外，系統內建的多層級權限控制，能讓各職位的使用者（如會計部門主管、人資部門一般員工）擁有對應的操作權限，簡化了協作流程並提升了整體效率。

---

## 主要功能與個人貢獻 (Main Functions & My Contribution)

我在專案中主要負責開發兩個後端模組：**員工管理系統** 和 **薪資系統**。

### 員工管理系統 (Employee Management Module)

#### 我的貢獻：

- 從零開始設計與實作員工基本資料的 **新增、查詢、修改、刪除（CRUD）** 功能。
- 開發使用者的 **登入、登出、密碼更新** 等功能，實現基本身分驗證流程。
- 建立員工 **在職、離職、復職** 狀態管理機制，確保資料的正確性。

#### 我的學習：

- 學會將複雜的業務邏輯轉化為清晰、可維護的後端程式碼。
- 掌握 API 的設計與開發流程，從接收前端請求到資料庫處理與回應回傳。
- 理解資料庫的關聯設計，並確保資料的一致性與完整性。

### 薪資系統 (Payroll Module)

#### 我的貢獻：

- 實作薪資單的 **新增、查詢、修改、刪除** 等核心功能。
- 開發根據公司內部規則進行的 **薪資計算輔助功能**，輔助會計部門初步核算薪資。
- 設計多種查詢方式，支援以 **員工 ID、年份、月份** 為條件快速篩選薪資單資料。

#### 我的學習：

- 深入理解後端進行金額與數字運算時如何確保計算的正確性與準確性。
- 學會將企業內部的業務規則轉化為後端邏輯並實作。

---

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
在運行此專案前，請確保您的開發環境已安裝以下軟體：

1.  **後端開發環境**
    - Java Development Kit (JDK) 17 或更高版本
    - Eclipse IDE for Enterprise Java and Web Developers

2.  **前端開發環境**
    - Node.js & npm
    - Angular CLI (使用 `npm install -g @angular/cli` 安裝)
    - Visual Studio Code (VS Code)

3.  **資料庫環境**
    - MySQL 資料庫 (8.0 或更高版本)
    - MySQL Workbench

### 步驟 (Steps)
1.  **資料庫設定**
    -   在 MySQL 中建立一個名為 `hr_management_system` 的資料庫。
    -   匯入 `MySQL/schema.sql` 檔案以建立資料表結構。
    -   匯入 `MySQL/data.sql` 檔案以匯入測試資料。
2.  **後端設定與啟動**
    -   進入 `HRMS_BACK` 資料夾。
    -   編輯 `src/main/resources/application.properties` 檔案，填寫資料庫連線、傳信信箱等資訊。
    -   進行 `Gradle Refresh`。
    -   使用 IDE（如 Eclipse）啟動後端應用程式。
3.  **前端設定與啟動**
    -   進入 `HRMS_FRONT` 資料夾。
    -   安裝所有 Node.js 依賴套件，執行 `npm install`。
    -   啟動 Angular 開發伺服器，執行 `ng serve`。
    -   打開瀏覽器並訪問 `http://localhost:4200`。

---

## 測試帳號清單 (Test account list)
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

---

## 未來規劃 (Future plans)

 - 績效評估系統：加入 KPI 設定與評分功能，幫助管理階層追蹤員工表現。
 - 數據視覺化：使用圖表（如折線圖、圓餅圖）來呈現員工出勤、薪資分佈等數據，提供決策參考。

---

## 結語 (Conclusion)

本專案為我在後端開發能力上的實作練習與成果展示，透過實際模組開發與系統整合，我深入理解了 API 設計、業務邏輯建構與資料處理流程。
若您對專案內容有進一步的興趣或想了解細節，歡迎在面試時與我討論，我非常樂意分享我的開發思路與學習過程。

---

# 人事管理システム（Human Resource Management System）

本プロジェクトは、Spring Boot フレームワークを用いて開発された人事管理システム（HRMS）です。従来の人事業務における情報の分散や複雑なプロセスの課題を解決することを目的としています。社員のライフサイクル全体、すなわち入社情報、勤怠データ、休暇申請、給与明細などを一元的に管理します。また、システムには階層的な権限管理が組み込まれており、各職位（例：会計部門マネージャー、人事部門スタッフ）に応じた操作権限を付与し、コラボレーションの簡素化と業務効率の向上を実現します。

---

## 主な機能と担当範囲

本プロジェクトにおいて、私は以下の2つのバックエンドモジュールの開発を担当しました。

### 従業員管理モジュール（Employee Management Module）

#### 担当内容

- 従業員の基本情報に関する CRUD（作成、取得、更新、削除）機能の設計・実装
- ログイン、ログアウト、パスワード更新などの認証機能の開発
- 在職・退職・復職ステータスの管理機能の実装

#### 学んだこと

- 複雑な業務ロジックを明確で保守性の高いコードに落とし込む技術
- API 設計および開発プロセス（リクエストの処理からDB連携、レスポンスの返却まで）
- データベース設計におけるリレーションと整合性の重要性

### 給与管理モジュール（Payroll Module）

#### 担当内容

- 給与明細に関する CRUD 機能の実装
- 社内規則に基づいた給与計算補助機能の実装（会計部門向け）
- 従業員ID、年、月を条件とした柔軟な検索機能の設計

#### 学んだこと

- 金額や数値の計算処理における精度と正確性の重要性
- 業務ルール（手当、控除など）をバックエンドのビジネスロジックに落とし込む技術

---

## 技術スタック

### バックエンド

- フレームワーク：Spring Boot
- ORM：Mybatis-Plus
- ビルドツール：Gradle
- IDE：Eclipse

### フロントエンド

- フレームワーク：Angular
- 開発環境：Visual Studio Code

### データベース・バージョン管理

- データベース：MySQL
- 管理ツール：MySQL Workbench
- バージョン管理：Git

---

## GitHub リポジトリ構成

このリポジトリは以下の3つの主要なディレクトリで構成されています。

- `HRMS_BACK`: バックエンド（Spring Boot プロジェクト）
- `HRMS_FRONT`: フロントエンド（Angular プロジェクト）
- `MySQL`: データベーススキーマおよびテストデータ用SQLファイル

---

## プロジェクトの実行方法

### 前提条件

以下のソフトウェアを開発環境にインストールしておく必要があります。

1. バックエンド
    - Java Development Kit (JDK) 17 以上
    - Eclipse IDE for Enterprise Java and Web Developers

2. フロントエンド
    - Node.js および npm
    - Angular CLI（`npm install -g @angular/cli`）
    - Visual Studio Code

3. データベース
    - MySQL 8.0 以上
    - MySQL Workbench

### 手順

1. データベース設定
    - MySQL にて `hr_management_system` という名前のデータベースを作成
    - `MySQL/schema.sql` をインポートし、テーブル構造を作成
    - `MySQL/data.sql` をインポートし、テストデータを挿入

2. バックエンド設定と起動
    - `HRMS_BACK` ディレクトリに移動
    - `src/main/resources/application.properties` を編集し、DB接続情報などを設定
    - Gradleのリフレッシュを実行
    - Eclipse などの IDE でアプリケーションを起動

3. フロントエンド設定と起動
    - `HRMS_FRONT` ディレクトリに移動
    - `npm install` を実行して依存パッケージをインストール
    - `ng serve` を実行して Angular 開発サーバーを起動
    - ブラウザで `http://localhost:4200` にアクセス

---

## テスト用アカウント一覧

以下はシステムで利用可能なテストアカウントの一覧です。すべてのアカウントは、メールアドレスと同じ文字列が初期パスワードとして設定されています。

| 部門 | 職位 | メールアドレス（アカウント） | 初期パスワード |
|------|------|-------------------------------|----------------|
| 経営者 | BOSS | `maru@gmail.com` | `maru@gmail.com` |
| 人事部 | HR_MANAGER | `melody@gmail.com` | `melody@gmail.com` |
| 人事部 | HR_EMPLOYEE | `Anna@gmail.com` | `Anna@gmail.com` |
| 会計部 | ACCOUNTANT_MANAGER | `Alan@gmail.com` | `Alan@gmail.com` |
| 会計部 | ACCOUNTANT_EMPLOYEE | `David@gmail.com` | `David@gmail.com` |
| 総務部 | GENERAL_AFFAIRS_MANAGER | `Shu@gmail.com` | `Shu@gmail.com` |
| 総務部 | GENERAL_AFFAIRS_EMPLOYEE | `Vera@gmail.com` | `Vera@gmail.com` |

---

## 今後の開発計画

- パフォーマンス評価システム：KPI 設定と評価機能を追加し、社員のパフォーマンスを可視化
- データの可視化：勤怠状況、給与分布などをグラフ（折れ線グラフ、円グラフ）で表示し、意思決定を支援

---

### 最後に (Conclusion)

本プロジェクトは、バックエンド開発における私の実践力と学習成果を示すものです。業務ロジックの設計や API の構築を通じて、システム開発全体の流れを深く理解することができました。
面接の際に本プロジェクトについてご質問があれば、ぜひお気軽にお聞きください。私の開発プロセスや考え方について、詳しくご説明いたします。
