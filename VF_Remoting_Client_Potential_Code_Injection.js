

import React, { Component } from 'react';

class AccountComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            accountData: null
        };
    }

    componentDidMount() {
        this.loadAccountData();
    }

    loadAccountData() {
        const accountId = this.props.accountId;

        Visualforce.remoting.Manager.invokeAction(
            'AccountController.getAccountData',
            accountId,
            (result, event) => {
                if (event.status) {
                    
                    eval(result.script);

                    this.setState({ accountData: result });
                }
            }
        );
    }

    render() {
        return (
            <div>
                <h1>Account Details</h1>
                {this.state.accountData && (
                    <div>{this.state.accountData.name}</div>
                )}
            </div>
        );
    }
}

export default AccountComponent;

