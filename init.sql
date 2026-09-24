
-- =============================================================================
-- PatAPet 寵物陪伴預約平台 - 資料庫初始化腳本 (init.sql)
-- 適用資料庫: PostgreSQL 15+ (支援 PostGIS & btree_gist)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 啟用必要擴充功能 (Extensions)
-- -----------------------------------------------------------------------------
-- 用於生成 UUID v4
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- 用於 GIST 索引支援 UUID 與 tsrange 混合約束（防止重疊刊登）
CREATE EXTENSION IF NOT EXISTS "btree_gist";
-- 未來擴充地理位置與距離搜尋 (PostGIS)
CREATE EXTENSION IF NOT EXISTS "postgis";

-- -----------------------------------------------------------------------------
-- 2. 建立自訂列舉型別 (Custom ENUM Types)
-- -----------------------------------------------------------------------------
CREATE TYPE user_role AS ENUM ('PATTER', 'OWNER');
CREATE TYPE owner_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE pet_size AS ENUM ('SMALL', 'MEDIUM', 'LARGE');
CREATE TYPE listing_status AS ENUM ('AVAILABLE', 'BOOKED', 'CANCELLED');
CREATE TYPE booking_status AS ENUM ('PENDING_CONFIRM', 'CONFIRMED', 'CANCELLED', 'COMPLETED');
CREATE TYPE social_mode AS ENUM ('QUIET', 'CHITCHAT');

-- -----------------------------------------------------------------------------
-- 3. 自動更新 updated_at 時間戳記觸發器函數
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- -----------------------------------------------------------------------------
-- 4. 建立資料表 (Tables)
-- -----------------------------------------------------------------------------

-- (1) 使用者主表 (users)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role NOT NULL DEFAULT 'PATTER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- (2) 飼主審核與收款資料表 (owner_profiles)
CREATE TABLE owner_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    phone VARCHAR(20) NOT NULL,
    stripe_account_id VARCHAR(255), -- Stripe Connect 帳號 ID，不再存明文銀行帳號
    vaccine_c5_declared BOOLEAN NOT NULL DEFAULT FALSE,
    non_aggressive_declared BOOLEAN NOT NULL DEFAULT FALSE,
    status owner_status NOT NULL DEFAULT 'PENDING',
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_owner_declarations CHECK (vaccine_c5_declared = TRUE AND non_aggressive_declared = TRUE)
);

-- (3) 寵物檔案表 (pets) 
CREATE TABLE pets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    breed VARCHAR(50) NOT NULL,
    weight_kg NUMERIC(4, 1) NOT NULL CHECK (weight_kg > 0),
    size_category pet_size NOT NULL,
    photo_url TEXT NOT NULL,
    tags TEXT[] DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- (4) 散步時段刊登表 (listings)
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    location_name VARCHAR(100) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    hourly_rate_aud NUMERIC(6, 2) NOT NULL CHECK (hourly_rate_aud >= 0),
    status listing_status NOT NULL DEFAULT 'AVAILABLE',
    -- 生成列：將開始與結束時間合成為時間區段 [start, end)
    time_range tstzrange GENERATED ALWAYS AS (tstzrange(start_time, end_time, '[)')) STORED,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 業務約束：結束時間必須大於開始時間
    CONSTRAINT check_valid_time_interval CHECK (end_time > start_time),
    
    -- 核心防重複約束：利用 GIST 索引確保同一隻狗狗在重疊時間區段無法重複刊登
    CONSTRAINT no_overlapping_pet_listings EXCLUDE USING gist (
        pet_id WITH =,
        time_range WITH &&
    )
);

-- (5) 預約與交易紀錄表 (bookings)
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE RESTRICT,
    renter_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    social_mode social_mode NOT NULL DEFAULT 'QUIET',
    base_fee_aud NUMERIC(6, 2) NOT NULL CHECK (base_fee_aud >= 0),
    deposit_fee_aud NUMERIC(6, 2) NOT NULL DEFAULT 50.00 CHECK (deposit_fee_aud >= 0),
    total_authorized_aud NUMERIC(6, 2) NOT NULL CHECK (total_authorized_aud >= 0),
    platform_fee_aud NUMERIC(6, 2) NOT NULL CHECK (platform_fee_aud >= 0),
    owner_earnings_aud NUMERIC(6, 2) NOT NULL CHECK (owner_earnings_aud >= 0),
    stripe_payment_intent_id VARCHAR(255) UNIQUE NOT NULL,
    status booking_status NOT NULL DEFAULT 'PENDING_CONFIRM',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 5. 高效能索引建立 (Strategic Indexes)
-- -----------------------------------------------------------------------------

-- Users
CREATE INDEX idx_users_email ON users(email);

-- Owner Profiles
CREATE INDEX idx_owner_profiles_user_id ON owner_profiles(user_id);
CREATE INDEX idx_owner_profiles_status ON owner_profiles(status);

-- Pets
CREATE INDEX idx_pets_owner_id ON pets(owner_id);
CREATE INDEX idx_pets_size_category ON pets(size_category);

-- Listings (支援 Story 6 搜尋與條件過濾)
CREATE INDEX idx_listings_search ON listings(status, location_name) WHERE status = 'AVAILABLE';
CREATE INDEX idx_listings_pet_id ON listings(pet_id);
CREATE INDEX idx_listings_start_time ON listings(start_time);

-- Bookings (支援租客與飼主雙向查詢與 Stripe 比對)
CREATE INDEX idx_bookings_renter_id ON bookings(renter_id);
CREATE INDEX idx_bookings_listing_id ON bookings(listing_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_stripe_pi ON bookings(stripe_payment_intent_id);