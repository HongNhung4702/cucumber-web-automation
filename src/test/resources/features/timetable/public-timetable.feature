
@public @timetable @lecturer-timetable
Feature: Tra cứu thời khóa biểu giảng viên công khai
  Là người dùng truy cập Cổng thông tin đào tạo
  Tôi muốn tra cứu thời khóa biểu giảng viên theo các điều kiện
  Để xem thông tin lịch giảng dạy phù hợp

  Background:
    Given người dùng đang truy cập Cổng thông tin đào tạo công khai

  @TC_37 @smoke
  Scenario: Truy cập trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn mục Thời khóa biểu giảng viên
    Then trang tra cứu thời khóa biểu giảng viên được hiển thị
    And hệ thống hiển thị các bộ lọc Năm học, Học kỳ, Khoa và Mã GV/Họ tên
    And hệ thống hiển thị nút Lọc và nút Làm mới
    And bảng thời khóa biểu có các cột thông tin lịch giảng dạy
    And bảng kết quả ban đầu chưa hiển thị dữ liệu lịch giảng dạy

  @TC_38 @search @positive
  Scenario: Tra cứu thời khóa biểu giảng viên với thông tin hợp lệ
    Given người dùng đang ở trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn năm học "2026-2027"
    And người dùng chọn học kỳ "HK1"
    And người dùng chọn khoa "Khoa Công nghệ thông tin"
    And người dùng nhập tên giảng viên "Phạm Trần Thiện"
    And người dùng nhấn nút Lọc
    Then hệ thống hiển thị thời khóa biểu phù hợp với điều kiện tra cứu
    And tiêu đề thời khóa biểu hiển thị năm học "2026-2027"
    And tiêu đề thời khóa biểu hiển thị học kỳ "HK01"
    And tiêu đề thời khóa biểu hiển thị khoa "Khoa Công nghệ thông tin"
    And các dòng kết quả thuộc giảng viên "Phạm Trần Thiện"
    And các dòng kết quả có thông tin mã lớp học phần và tên học phần
    And các dòng kết quả có thông tin thứ, tiết, phòng học và tuần học

  @TC_39 @search @negative
  Scenario: Tra cứu thời khóa biểu giảng viên với thông tin không hợp lệ
    Given người dùng đang ở trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn năm học "2026-2027"
    And người dùng chọn học kỳ "HK1"
    And người dùng chọn khoa "Khoa Công nghệ thông tin"
    And người dùng nhập mã hoặc tên giảng viên "@$#ff"
    And người dùng nhấn nút Lọc
    Then bảng kết quả hiển thị trạng thái "Chưa có lịch học"
    And không có dòng dữ liệu lịch giảng dạy nào được hiển thị
    And hệ thống không phát sinh lỗi trong quá trình tra cứu

  @TC_40 @reset
  Scenario: Làm mới các điều kiện tra cứu thời khóa biểu giảng viên
    Given người dùng đang ở trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn năm học "2026-2027"
    And người dùng chọn học kỳ "HK1"
    And người dùng chọn khoa "Khoa Công nghệ thông tin"
    And người dùng nhập tên giảng viên "Phạm Trần Thiện"
    And người dùng nhấn nút Lọc
    And người dùng nhấn nút Làm mới
    Then bộ lọc Năm học trở về giá trị "2026-2027"
    And bộ lọc Học kỳ trở về giá trị "HK1"
    And bộ lọc Khoa trở về giá trị "Trường Đại học Quy Nhơn"
    And trường Mã GV/Họ tên được xóa trắng

  @TC_41 @search @reset
  Scenario: Tra cứu lại sau khi làm mới điều kiện tìm kiếm
    Given người dùng đang ở trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn năm học "2026-2027"
    And người dùng chọn học kỳ "HK1"
    And người dùng chọn khoa "Khoa Công nghệ thông tin"
    And người dùng nhập tên giảng viên "Phạm Trần Thiện"
    And người dùng nhấn nút Lọc
    Then hệ thống hiển thị kết quả tra cứu tương ứng với bộ lọc đã chọn
    When người dùng nhấn nút Làm mới
    Then các điều kiện tra cứu trở về trạng thái mặc định
    When người dùng chọn lại năm học "2026-2027"
    And người dùng chọn lại học kỳ "HK1"
    And người dùng chọn lại khoa "Khoa Công nghệ thông tin"
    And người dùng nhập lại tên giảng viên "Phạm Trần Thiện"
    And người dùng nhấn nút Lọc
    Then tiêu đề thời khóa biểu hiển thị đúng năm học, học kỳ và khoa đã chọn
    And các dòng kết quả thuộc giảng viên "Phạm Trần Thiện"
    And kết quả tra cứu lần thứ hai nhất quán với kết quả lần thứ nhất

  @TC_42 @search @filter
  Scenario: Tra cứu liên tiếp với bộ lọc A, B và quay lại A
    Given người dùng đang ở trang tra cứu thời khóa biểu giảng viên
    When người dùng chọn năm học "2024-2025"
    And người dùng chọn học kỳ "HK2"
    And người dùng chọn khoa "Khoa Công nghệ thông tin"
    And người dùng nhập tên giảng viên "Lê Xuân Việt"
    And người dùng nhấn nút Lọc
    Then hệ thống hiển thị kết quả tương ứng với bộ lọc A
    When người dùng chọn năm học "2023-2024"
    And người dùng chọn học kỳ "HK1"
    And người dùng chọn khoa "Khoa Giáo dục thể chất"
    And người dùng nhập tên giảng viên "Bùi Trọng Duy"
    And người dùng nhấn nút Lọc
    Then tiêu đề thời khóa biểu hiển thị năm học "2023-2024"
    And tiêu đề thời khóa biểu hiển thị học kỳ "HK01"
    And tiêu đề thời khóa biểu hiển thị khoa "Khoa Giáo dục thể chất"
    And kết quả tra cứu tương ứng với giảng viên "Bùi Trọng Duy"
    And không còn hiển thị dữ liệu không phù hợp từ bộ lọc A
    When người dùng chọn lại năm học "2024-2025"
    And người dùng chọn lại học kỳ "HK2"
    And người dùng chọn lại khoa "Khoa Công nghệ thông tin"
    And người dùng nhập lại tên giảng viên "Lê Xuân Việt"
    And người dùng nhấn nút Lọc
    Then tiêu đề thời khóa biểu hiển thị năm học "2024-2025"
    And tiêu đề thời khóa biểu hiển thị học kỳ "HK02"
    And tiêu đề thời khóa biểu hiển thị khoa "Khoa Công nghệ thông tin"
    And kết quả cuối cùng nhất quán với kết quả bộ lọc A ở lần tra cứu đầu tiên
