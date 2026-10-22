
@student @exam @exam-schedule
Feature: Xem và tra cứu lịch thi sinh viên
  Là sinh viên đã đăng nhập vào Cổng thông tin đào tạo
  Tôi muốn tra cứu lịch thi theo năm học, học kỳ và thời gian
  Để theo dõi thông tin lịch thi liên quan đến tài khoản của mình

  Background:
    Given sinh viên đã đăng nhập thành công vào hệ thống
    And họ tên và mã sinh viên trên giao diện khớp với tài khoản kiểm thử

  @TC_28 @smoke @data-verification
  Scenario: Xem lịch thi theo năm học và học kỳ
    When sinh viên truy cập mục Lịch thi
    And sinh viên chọn tab Năm học-Học kỳ
    And sinh viên chọn năm học "2022-2023"
    And sinh viên chọn học kỳ "Học kỳ 1"
    Then hệ thống hiển thị khu vực lịch thi của sinh viên
    And bộ lọc Năm học hiển thị "2022-2023"
    And bộ lọc Học kỳ hiển thị "Học kỳ 1"
    And hệ thống hiển thị các bảng "Lịch chưa thi" và "Tất cả"
    And bảng lịch thi có các cột mã lớp học phần, tên lớp học phần, lần thi, ngày thi, giờ thi, phòng thi, địa điểm, hình thức thi và kỳ thi
    And danh sách lịch thi tương ứng với năm học và học kỳ đã chọn
    And không hiển thị bản ghi lịch thi thuộc năm học hoặc học kỳ khác
    And các thông tin học phần và lịch thi hiển thị thuộc phạm vi dữ liệu của tài khoản sinh viên đang đăng nhập

  @TC_29 @filter @data-verification
  Scenario: Xem lịch thi khi thay đổi năm học
    Given sinh viên đang ở tab Năm học-Học kỳ của trang Lịch thi
    And sinh viên đang chọn một năm học khác "2022-2023"
    When sinh viên thay đổi năm học sang "2022-2023"
    Then bộ lọc Năm học hiển thị "2022-2023"
    And danh sách lịch thi được cập nhật theo năm học đã chọn
    And không hiển thị bản ghi lịch thi thuộc năm học khác
    And không giữ lại các bản ghi của lần tra cứu trước không phù hợp với năm học mới
    And hệ thống hiển thị trạng thái không có lịch thi nếu không có bản ghi phù hợp

  @TC_30 @filter @data-verification
  Scenario: Xem lịch thi khi thay đổi học kỳ
    Given sinh viên đang ở tab Năm học-Học kỳ của trang Lịch thi
    And sinh viên đã chọn năm học "2022-2023"
    And sinh viên đang chọn một học kỳ khác "Học kỳ 3"
    When sinh viên thay đổi học kỳ sang "Học kỳ 3"
    Then bộ lọc Học kỳ hiển thị "Học kỳ 3"
    And danh sách lịch thi được cập nhật theo học kỳ đã chọn
    And không hiển thị bản ghi lịch thi thuộc học kỳ khác
    And không giữ lại các bản ghi của học kỳ trước không phù hợp với học kỳ mới
    And hệ thống hiển thị trạng thái không có lịch thi nếu không có bản ghi phù hợp

  @TC_31 @filter @date-range @data-verification
  Scenario: Xem lịch thi theo khoảng thời gian hợp lệ
    Given sinh viên đang ở trang Lịch thi
    When sinh viên chọn tab Thời gian
    And sinh viên nhập ngày bắt đầu "14/10/2024"
    And sinh viên nhập ngày kết thúc "09/09/2026"
    Then hệ thống hiển thị kết quả lịch thi theo khoảng thời gian đã chọn
    And các bản ghi lịch thi có ngày thi từ "14/10/2024" đến "09/09/2026"
    And không có bản ghi lịch thi nào nằm ngoài khoảng thời gian đã chọn
    And các bản ghi có thông tin học phần, ngày thi, giờ thi và phòng thi tương ứng khi có dữ liệu
    And thông tin lịch thi hiển thị thuộc phạm vi dữ liệu của tài khoản sinh viên đang đăng nhập

  @TC_32 @negative @date-range
  Scenario: Kiểm tra tra cứu lịch thi khi ngày bắt đầu lớn hơn ngày kết thúc
    Given sinh viên đang ở trang Lịch thi
    When sinh viên chọn tab Thời gian
    And sinh viên nhập ngày bắt đầu "14/10/2024"
    And sinh viên nhập ngày kết thúc "09/09/2023"
    Then hệ thống hiển thị trạng thái không có dữ liệu lịch thi
    And không có bản ghi lịch thi nào được hiển thị
    And không phát sinh lỗi trong quá trình tra cứu
