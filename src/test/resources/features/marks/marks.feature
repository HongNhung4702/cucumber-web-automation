
@student @marks @student-marks
Feature: Xem kết quả học tập theo năm học và học kỳ
  Là sinh viên đã đăng nhập
  Tôi muốn xem kết quả học tập theo chương trình đào tạo, năm học và học kỳ
  Để theo dõi kết quả học tập được hệ thống cung cấp cho tài khoản của mình

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_33 @ui
  Scenario: Hiển thị bảng điểm theo điều kiện tra cứu
    When sinh viên tra cứu điểm ở chế độ "Xem điểm sinh viên" với chương trình "Kỹ thuật phần mềm", năm học "2025-2026" và học kỳ "Học kỳ 2"
    Then các bộ lọc xem điểm hiển thị đúng điều kiện đã chọn
    And bảng điểm hiển thị các cột đã được xác nhận

  @TC_33 @data-verification
  Scenario: Đối chiếu danh sách học phần với dữ liệu tham chiếu
    When sinh viên tra cứu điểm ở chế độ "Xem điểm sinh viên" với chương trình "Kỹ thuật phần mềm", năm học "2025-2026" và học kỳ "Học kỳ 2"
    Then số lượng học phần khớp dữ liệu tham chiếu của TC_33
    And toàn bộ dữ liệu học phần khớp dữ liệu tham chiếu của TC_33

  @TC_33 @summary-verification
  Scenario: Đối chiếu kết quả tổng hợp của học kỳ
    When sinh viên tra cứu điểm ở chế độ "Xem điểm sinh viên" với chương trình "Kỹ thuật phần mềm", năm học "2025-2026" và học kỳ "Học kỳ 2"
    Then các chỉ tiêu tổng hợp học kỳ khớp dữ liệu tham chiếu của TC_33
