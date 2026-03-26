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

                    // ✅ SAFE: No eval
                    this.handleServerAction(result);

                    this.setState({ accountData: result });
                }
            }
        );
    }

    /**
     * ✅ Controlled handling of server-driven behavior
     * Only predefined actions are allowed
     */
    handleServerAction(result) {
        if (!result || !result.action) return;

        const actions = {
            SHOW_ALERT: (payload) => alert(payload),
            LOG: (payload) => console.log(payload),
            // Add more SAFE actions if needed
        };

        if (actions[result.action]) {
            actions[result.action](result.payload);
        } else {
            console.warn('Unknown action received from server:', result.action);
        }
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
