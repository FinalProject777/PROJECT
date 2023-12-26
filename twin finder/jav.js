'use strict';

const cards = {
    box1: 'box2',
    box2: 'box1',
    box3: 'box4',
    box4: 'box3',
    box5: 'box6',
    box6: 'box5',
    box7: 'box8',
    box8: 'box7',
    box9: 'box10',
    box10: 'box9',
    box11: 'box12',
    box12: 'box11',
    box13: 'box14',
    box14: 'box13',
};

const carbon = document.querySelector('.carbon');
const time = document.querySelector('.time');
const counter = document.querySelector('.counter');
const container = document.querySelector('.container');
const box = Array.from(document.querySelectorAll('.box'));
const audio = new Audio('https://loudlinks.rocks/sounds/mp3/magic.mp3');

let last_flipped = [];
let correct_flips = 0;

let moves = 0;
let seconds = 0;
let minutes = 0;
let seconds_str = '';
let minutes_str = '';
let timer_observer;

container.innerHTML = '';

// Function to display game over modal
function gameOverModal(isVictory, moves, minutes, seconds) {
    const gameOver = (isVictory) => {
    
        const modalText = isVictory ? `You found the word:` : 'The correct word was:';
        gameModal.querySelector("img").src = `images/${isVictory ? 'victory' : 'lost'}.gif`;
        gameModal.querySelector("h4").innerText = isVictory ? 'Congrats!' : 'Game Over!';
        gameModal.querySelector("p").innerHTML = `${modalText} <b>${currentWord}</b>`;
        gameModal.classList.add("show");
       
    }
    
    document.getElementById('modalTitle').innerText = modalText;
    document.getElementById('modalMessage').innerText = modalMessage;
    modal.style.display = 'block';
}


// Function to handle card flips


function compareFlipped(array) {
    // Reset if more than 2 cards are flipped
    if (array.length > 2) {
        array.forEach(el => el.classList.remove('flipped'));
        last_flipped = [];
    }

    // Check if exactly 2 cards are flipped
    if (array.length === 2) {
        const card1 = array[0].classList[1];
        const card2 = array[1].classList[1];

        // Check for a match
        if (cards[card1] === card2 || cards[card2] === card1) {
            // Mark cards as matching
            document.getElementsByClassName(card1)[0].firstElementChild.classList.add('matchingcards');
            document.getElementsByClassName(card2)[0].firstElementChild.classList.add('matchingcards');
            
            // Update counters
            correct_flips += 1;
            last_flipped = [];

            // Check for game completion after a successful match
            if (correct_flips >= Object.keys(cards).length / 2) {
                // Assuming 'audio' and 'gameOverModal' are defined
                audio.play();
                clearInterval(timer_observer);
                gameOverModal(isVictory); // You need to define isVictory
                return;
            }
        } else {
            // No match, flip cards back after a delay
            setTimeout(() => {
                array.forEach(el => el.classList.remove('flipped'));
                last_flipped = [];
            }, 700);
        }
    }
    // Handle other cases if needed
}
function spreadCards(array) {
    let new_Arr = array.filter(el => array.indexOf(el) % 2 == 0);
    while (0 < new_Arr.length) {
        const num = Math.floor(Math.random() * new_Arr.length);
        const pick = new_Arr[num];
        container.appendChild(pick);
        new_Arr.splice(num, 1);
    }
}

function startWatching(seconds, minutes) {
    timer_observer = setInterval(() => {
        seconds > 58 ? ((minutes += 1), (seconds = 0)) : (seconds += 1);
        seconds_str = seconds > 9 ? `${seconds}` : `0${seconds}`;
        minutes_str = minutes > 9 ? `${minutes}` : `0${minutes}`;
        time.innerHTML = `${minutes_str}:${seconds_str}`;
        
        if (correct_flips >= Object.keys(cards).length / 2) {
            audio.play();
            handleGameCompletion(true);
            return;
        }
    }, 1000);
}

function startGame() {
    carbon.style.height = '50%';
    carbon.style.width = '77%';

    correct_flips = 0;
    last_flipped = [];
    moves = 0;
    seconds = 0;
    minutes = 0;
    seconds_str = '';
    minutes_str = '';
    time.innerHTML = 'XX:XX';
    counter.innerHTML = '0';
    container.innerHTML = '';
    spreadCards(box);
    container.childNodes.forEach(node =>
        node.firstElementChild.classList.remove('matchingcards')
    );
    box.forEach(el => el.addEventListener('click', flipOnClick)); // re-add click event
    clearInterval(timer_observer);
    startWatching(seconds, minutes);
}

function flipOnClick(e) {
    moves++;
    counter.innerHTML = moves;
    const element = e.target;
    last_flipped.push(element);
    element.classList.add('flipped');
    compareFlipped(last_flipped);
}

function gameWonParty(moves, minutes, seconds) {
    audio.play();
    const totalTime = `${minutes > 9 ? minutes : '0' + minutes}:${seconds > 9 ? seconds : '0' + seconds}`;
    document.getElementById('totalTime').innerText = totalTime;
    document.getElementById('totalMoves').innerText = moves;
    const modal = document.getElementById('myModal');
    modal.style.display = 'block';
}

function closeModal() {
    const modal = document.getElementById('myModal');
    modal.style.display = 'none';
}



box.forEach(el => el.addEventListener('click', flipOnClick));
