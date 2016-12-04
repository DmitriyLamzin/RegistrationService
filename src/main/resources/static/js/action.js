var $body;
$(document).ready(function() {
    $body = $('body');
    $body.on('click', '#formRequest', function (event) {
        event.preventDefault();
        $body.load($(this).attr('href'));
    });

    $body.on('submit', '#registrationForm', function (event) {
        event.preventDefault();

        $.ajax({
            url: ($(this).attr('action')), // url where to submit the request
            type: "POST", // type of action POST || GET
            data: $(this).serialize(),
            success: function(data) {
                $body.empty();
                $body.append(data);
            },
            error: function (xhr, resp, text) {
                console.log(xhr);
                $body.empty();
                $body.append(xhr.responseText);
            }
        });
        $body.load($(this).attr('href'));
    });
});

