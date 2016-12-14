M = csvread('Sentiments.txt');
M(339:end,1) = M(339:end, 1) .* 2;

[length, d] = size(M);
R = M(:, 1); % Website ratings
S = M(:, 2); % Sentiment analysis

Y = [];
x = [];
j = 1;
tmp = [];
for i = 1:length
    if (i >= 2) && (R(i) ~= R(i-1))
        x(j) = mean(tmp);
        Y(j) = R(i);
        j = j+1;
        tmp = [];
    end
    if (S(i) ~= 2) % ignore neutral sentiment
        tmp = [tmp, S(i)];
    end
end

[d, num] = size(x);
i = linspace(1, num, num);

hold on
grid on
plot(i, Y);
plot(i, x/4.0*10);
legend('Websites ratings', 'Sentimented')
xlabel('Object ID')
ylabel('Ratings')
hold off
