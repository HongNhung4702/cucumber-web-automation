
@student @marks @mark-term-switch
Feature: Kiểm thử thay đổi học kỳ có điểm sang chưa có điểm
  Là sinh viên đã đăng nhập
  Tôi muốn thay đổi học kỳ khi xem điểm
  Để xác minh dữ liệu được cập nhật và khôi phục chính xác

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_36 @filter @baseline
  Scenario: Chuyển từ học kỳ có điểm sang chưa có điểm rồi quay lại
    When sinh viên ghi nhận điểm học kỳ A TC_36
    And sinh viên chuyển sang học kỳ B TC_36
    Then học kỳ B TC_36 hiển thị học phần chưa có điểm
    When sinh viên quay lại học kỳ A TC_36
    Then dữ liệu học kỳ A TC_36 khôi phục nguyên vẹn
